package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.*;
import com.ssg.ssgproductapi.dto.ProductDTO;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ProductRepository;
import com.ssg.ssgproductapi.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UserService userService;
    private final PromotionService promotionService;
    private final ProductRepository productRepository;
    private final AppliedPromotionService appliedPromotionService;
    private final ValidateUtil validateUtil;

    @Transactional
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("can not find user by productId: " + productId));
    }


    @Transactional
    public void registerProduct(Long userId, String name, String description, int fullPrice, String authority,
                                String start, String end) {

        User user = userService.getUser(userId);
        LocalDateTime startedAt = LocalDateTime.parse(start,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endAt = LocalDateTime.parse(end,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (endAt.isBefore(startedAt)) {
            throw new InvalidArgsException("종료시점이 시작시점보다 빠릅니다.");
        }

        UserType userType = UserType.일반회원;
        if (authority.equals(UserType.기업회원.toString())) {
            userType = UserType.기업회원;
        }
        Product product = Product.builder()
                .name(name)
                .description(description)
                .fullPrice(fullPrice)
                .authority(userType)
                .startedAt(startedAt)
                .endAt(endAt)
                .user(user)
                .build();

        validateUtil.validate(product);
        productRepository.save(product);
    }


    @Transactional
    public List<ProductDTO.UserProduct> getProducts(Long userId) {
        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
        List<ProductDTO.UserProduct> userProducts = new ArrayList<>();
        List<Product> productList;
        LocalDateTime now = LocalDateTime.now();
        if (user.getUserType().contains(UserType.기업회원)) {
            productList = productRepository.getAllByStartedAtGreaterThanEqualAndEndAtLessThanEqual(now, now);
        } else {
            productList = productRepository.getAllByAuthorityAndStartedAtGreaterThanEqualAndEndAtLessThanEqual(UserType.일반회원, now, now);
        }

        for (Product product : productList) {
            Long appliedPromotionId = product.getPromotion().getId();
            // 상품에 적용된 프로모션이 종료되었을 경우
            if (!promotionService.isValidPromotion(appliedPromotionId)) {
                product.updateDiscountedPrice(product.getFullPrice());
                // 적용된 프로모션중에 가장 할인이 많이 되는 프로모션으로 교체
                appliedPromotionService.dirtyCheckAppliedPromotion(product.getId());
            }
            userProducts.add(ProductDTO.UserProduct.builder()
                    .id(product.getId())
                    .description(product.getDescription())
                    .discountedPrice(product.getDiscountedPrice())
                    .fullPrice(product.getFullPrice())
                    .createdAt(product.getCreatedAt())
                    .promotionId(product.getPromotion().getId())
                    .sellerId(product.getUser().getId())
                    .name(product.getName())
                    .endAt(product.getEndAt())
                    .build());
        }

        return userProducts;
    }

    @Override
    @Transactional
    public void updateProduct(Long userId, Long productId, String authority, String description, String name, Integer price, String start, String end) {
        // TODO: 프로모션 즉시조인으로 가져오는 쿼리로 가져올 것
        Product updatedProduct = this.getProduct(productId);

        if (!updatedProduct.getUser().getId().equals(userId)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }
        final String updatedName = StringUtils.isNotBlank(name) ? name : updatedProduct.getName();
        final String updatedDescription = StringUtils.isNotBlank(description) ? description : updatedProduct.getDescription();
        UserType updatedAuthority;
        updatedAuthority = updatedProduct.getAuthority();
        if (ObjectUtils.isNotEmpty(authority)){
            updatedAuthority = UserType.기업회원;
            if (authority.equals(UserType.일반회원.toString())) {
                updatedAuthority = UserType.일반회원;
            }
        }
        LocalDateTime updatedStartedAt = updatedProduct.getStartedAt();
        if (ObjectUtils.isNotEmpty(start)) {
            updatedStartedAt = LocalDateTime.parse(start,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        LocalDateTime updatedEndAt = updatedProduct.getEndAt();
        if (ObjectUtils.isNotEmpty(end)) {
            updatedEndAt = LocalDateTime.parse(end,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (updatedEndAt.isBefore(updatedStartedAt)) {
            throw new InvalidArgsException("종료시점이 시작시점보다 빠릅니다.");
        }

        // 값을 수정할 경우
        if (price != null){
            updatedProduct.updateFullPrice(price);
            // 정액제 2000원, 정률제 10% 프로모션이 적용된 상품이 있다고 가정, 기존에는 정액제 프로모션이 가장 할인이 많이 되는 프로모션
            // BUT, 10000원짜리 상품을 30000만원으로 변경했을 경우 정률제의 할인률이 더 높아짐
            // SO, 적용된 프로모션들을 확인 하는 과정
            updatedProduct.updateDiscountedPrice(updatedProduct.getFullPrice());
            // 적용된 프로모션중에 가장 할인이 많이 되는 프로모션으로 교체
            appliedPromotionService.dirtyCheckAppliedPromotion(updatedProduct.getId());
        }

        updatedProduct.updateProduct(
                updatedName,
                updatedDescription,
                updatedAuthority,
                updatedStartedAt,
                updatedEndAt);

        validateUtil.validate(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = this.getProduct(productId);
        if (!product.getUser().getId().equals(userId)) {
            throw new ForbiddenException("삭제권한이 없습니다.");
        }
        productRepository.delete(product);
    }
}
