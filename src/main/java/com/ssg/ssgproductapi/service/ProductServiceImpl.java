package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.*;
import com.ssg.ssgproductapi.dto.ProductDTO;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ProductRepository;
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
public class ProductServiceImpl {

    private final UserService userService;
    private final ProductRepository productRepository;

    @Transactional
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("can not find user by productId: " + productId));

    }


    @Transactional
    public void registerProduct(String name, String description, int fullPrice, UserType authority,
                                String start, String end) {

        LocalDateTime startedAt = LocalDateTime.parse(start,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endAt = LocalDateTime.parse(end,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Product product = Product.builder()
                .name(name)
                .description(description)
                .fullPrice(fullPrice)
                .authority(authority)
                .startedAt(startedAt)
                .endAt(endAt)
                .build();

        productRepository.save(product);
    }


    @Transactional
    public List<ProductDTO.userProduct> getProducts(Long userId) {
        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
        List<ProductDTO.userProduct> userProducts = new ArrayList<>();
        List<Product> productList;
        LocalDateTime now = LocalDateTime.now();
        if (user.getRoleSet().contains(UserType.기업회원)) {
            productList = productRepository.getAllByStartedAtGreaterThanEqualAndEndAtLessThanEqual(now, now);
        } else {
            productList = productRepository.getAllByAuthorityAndStartedAtGreaterThanEqualAndEndAtLessThanEqual(UserType.일반회원, now, now);
        }

        for (Product product : productList) {
            userProducts.add(ProductDTO.userProduct.builder()
                    .id(product.getId())
                    .description(product.getDescription())
                    .discountedPrice(product.getDiscountedPrice())
                    .fullPrice(product.getFullPrice())
                    .createdAt(product.getCreatedAt())
                    .promotionId(product.getPromotion().getId())
                    .sellerId(product.getSeller().getId())
                    .name(product.getName())
                    .endAt(product.getEndAt())
                    .build());
        }

        return userProducts;
    }

    @Transactional
    public void updateProduct(Long userId, Long productId, Product product) {
        // TODO: 프로모션 즉시조인으로 가져오는 쿼리로 가져올 것
        Product updatedProduct = this.getProduct(productId);

        if (!updatedProduct.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        final String updatedName = StringUtils.isNotBlank(product.getName()) ? product.getName() : updatedProduct.getName();
        final String updatedDescription = StringUtils.isNotBlank(product.getDescription()) ? product.getDescription() : updatedProduct.getDescription();
        final int updatedFullPrice = ObjectUtils.isNotEmpty(product.getFullPrice()) ? product.getFullPrice() : updatedProduct.getFullPrice();
        final UserType updatedAuthority = ObjectUtils.isNotEmpty(product.getAuthority()) ? product.getAuthority() : updatedProduct.getAuthority();
        final LocalDateTime updatedStartedAt = ObjectUtils.isNotEmpty(product.getStartedAt()) ? product.getStartedAt() : updatedProduct.getStartedAt();
        final LocalDateTime updatedEndAt = ObjectUtils.isNotEmpty(product.getEndAt()) ? product.getEndAt() : updatedProduct.getEndAt();
        int updatedDiscountedPrice = product.getFullPrice();

        if (updatedProduct.getPromotion() != null) {
            Promotion promotion = updatedProduct.getPromotion();
            DiscountPolicy policy = promotion.getPolicy();
            if (policy.equals(DiscountPolicy.FIXED)) {
                double rate = (double) (100 - promotion.getDiscountRate()) / 100;
                updatedDiscountedPrice = (int) (updatedFullPrice * rate);
            } else if (policy.equals(DiscountPolicy.FLAT)) {
                updatedDiscountedPrice = updatedFullPrice - promotion.getDiscountRate();
            }
        }
        updatedProduct.updateProduct(
                updatedName,
                updatedDescription,
                updatedFullPrice,
                updatedDiscountedPrice,
                updatedAuthority,
                updatedStartedAt,
                updatedEndAt);
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = this.getProduct(productId);
        if (!product.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("삭제권한이 없습니다.");
        }
        productRepository.delete(product);
    }
}
