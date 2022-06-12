package com.ssg.ssgproductapi.service;

import com.google.common.base.Preconditions;
import com.ssg.ssgproductapi.domain.*;
import com.ssg.ssgproductapi.dto.ProductRespDTO;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ProductRepository;
import com.ssg.ssgproductapi.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
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
    private final ApplyPromotionService appliedPromotionService;
    private final ValidateUtil validateUtil;

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("can not find product by productId: " + productId));
    }

    /**
     * 내가 등록한 상품들 조회
     * @param userId
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductRespDTO.MyProduct> getMyProducts(Long userId, Pageable pageable) {
        List<Product> products = productRepository.getAllByUser_Id(userId, pageable);
        List<ProductRespDTO.MyProduct> myProducts = new ArrayList<>();
        for (Product product : products) {
            myProducts.add(ProductRespDTO.MyProduct.builder()
                    .productId(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .fullPrice(product.getFullPrice())
                    .discountedPrice(product.getDiscountedPrice())
                    .startedAt(product.getStartedAt())
                    .endAt(product.getEndAt())
                    .promotionId((product.getPromotion() != null) ? product.getPromotion().getId() : null)
                    .promotionName((product.getPromotion() != null) ? product.getPromotion().getName() : null)
                    .build());
        }

        return myProducts;

    }

    /**
     * 단일 상품 조회
     * @param userId
     * @param productId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ProductRespDTO.Product getProductDTO(Long userId, Long productId) {
        Product product = productRepository.getProductById(productId);
        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        if (product.getAuthority().equals(UserType.기업회원)) {
            if (!user.getUserType().contains(UserType.기업회원)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        }
        return ProductRespDTO.Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .fullPrice(product.getFullPrice())
                .discountedPrice(product.getDiscountedPrice())
                .startedAt(product.getStartedAt())
                .username(product.getUser().getName())
                .endAt(product.getEndAt())
                .build();
    }


    /**
     * 상품등록
     * @param userId
     * @param name
     * @param description
     * @param fullPrice 원가
     * @param authority 일반회원 or 기업회원
     * @param start 시작시점
     * @param end 종료시점
     * @return
     */
    @Override
    @Transactional
    public Long registerProduct(Long userId, String name, String description, int fullPrice, String authority,
                                String start, String end) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(name, "productId은 필수값 입니다.");
        Preconditions.checkNotNull(description, "description은 필수값 입니다.");
        Preconditions.checkNotNull(authority, "authority은 필수값 입니다.");
        Preconditions.checkNotNull(start, "start은 필수값 입니다.");
        Preconditions.checkNotNull(end, "end은 필수값 입니다.");

        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
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
        return product.getId();
    }

    /**
     * 일반회원용 상품들 조회
     * @param userId
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductRespDTO.UserProduct> getNormalProducts(Long userId, Pageable pageable) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");

        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
        LocalDateTime now = LocalDateTime.now();
        List<Product> productList = productRepository.getAllByAuthorityAndStartedAtLessThanEqualAndEndAtGreaterThanEqual(UserType.일반회원, now, now, pageable);

        return this.getProductDTO(productList);

    }

    /**
     * 기업회원용 상품 조회
     * @param userId
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductRespDTO.UserProduct> getEnterpriseProducts(Long userId, Pageable pageable) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
        if (!user.getUserType().contains(UserType.기업회원)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Product> productList = productRepository.getAllByAuthorityAndStartedAtLessThanEqualAndEndAtGreaterThanEqual(UserType.기업회원, now, now, pageable);
        return this.getProductDTO(productList);
    }

    /**
     * 상품 수정 (userId, productId를 제외한 모든 값이 nullable)<br>
     * 가격을 수정하면 더 할인율이 높은 프로모션이 있는지 찾는 과정이 실행
     * @param userId
     * @param productId
     * @param authority
     * @param description
     * @param name
     * @param price
     * @param start
     * @param end
     * @return
     */
    @Override
    @Transactional
    public Long updateProduct(Long userId, Long productId, String authority, String description, String name, Integer price, String start, String end) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(productId, "productId은 필수값 입니다.");
        Product updatedProduct = this.getProduct(productId);

        if (!updatedProduct.getUser().getId().equals(userId)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
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
        final String updatedName = StringUtils.isNotBlank(name) ? name : updatedProduct.getName();
        final String updatedDescription = StringUtils.isNotBlank(description) ? description : updatedProduct.getDescription();
        UserType updatedAuthority;
        updatedAuthority = updatedProduct.getAuthority();
        if (ObjectUtils.isNotEmpty(authority)) {
            updatedAuthority = UserType.기업회원;
            if (authority.equals(UserType.일반회원.toString())) {
                updatedAuthority = UserType.일반회원;
            }
        }
        // 값을 수정할 경우
        if (price != null) {
            updatedProduct.updateFullPrice(price);
            // 정액제 2000원, 정률제 10% 프로모션이 적용된 상품이 있다고 가정, 기존에는 정액제 프로모션이 가장 할인이 많이 되는 프로모션
            // BUT, 10000원짜리 상품을 30000만원으로 변경했을 경우 정률제의 할인률이 더 높아짐
            // SO, 적용된 프로모션들을 확인 하는 과정
            updatedProduct.updateDiscountedPrice(updatedProduct.getFullPrice());
            // 적용된 프로모션중에 가장 할인이 많이 되는 프로모션으로 교체
            if (updatedProduct.getPromotion() != null) {
                appliedPromotionService.dirtyCheckAppliedPromotion(updatedProduct.getId());
            }
        }

        updatedProduct.updateProduct(
                updatedName,
                updatedDescription,
                updatedAuthority,
                updatedStartedAt,
                updatedEndAt);

        validateUtil.validate(updatedProduct);
        return updatedProduct.getId();
    }

    /**
     * 상품 삭제
     * @param userId
     * @param productId
     */
    @Override
    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(productId, "productId은 필수값 입니다.");
        Product product = this.getProduct(productId);
        if (!product.getUser().getId().equals(userId)) {
            throw new ForbiddenException("삭제권한이 없습니다.");
        }
        productRepository.delete(product);
    }

    /**
     * 상품의 노출 시점 뿐만 아니라 <br>
     * 현재 적용된 프로모션의 시점도 확인 해야 하기 때문에 <br>
     * 프로모션이 유효한지 체크하는 메서드
     * @param productList
     * @return
     */
    private List<ProductRespDTO.UserProduct> getProductDTO(List<Product> productList) {
        List<ProductRespDTO.UserProduct> products = new ArrayList<>();
        for (Product product : productList) {
            // 상품에 적용된 프로모션이 있고,
            if (product.getPromotion() != null) {
                Long appliedPromotionId = product.getPromotion().getId();
                // 상품에 적용된 프로모션이 종료되었을 경우
                if (!promotionService.isValidPromotion(appliedPromotionId)) {
                    product.updateDiscountedPrice(product.getFullPrice());
                    // 적용된 프로모션중에 가장 할인이 많이 되는 프로모션으로 교체
                    appliedPromotionService.dirtyCheckAppliedPromotion(product.getId());
                }
            }
            products.add(ProductRespDTO.UserProduct.builder()
                    .productId(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .discountedPrice(product.getDiscountedPrice())
                    .fullPrice(product.getFullPrice())
                    .createdAt(product.getCreatedAt())
                    .startAt(product.getStartedAt())
                    .endAt(product.getEndAt())
                    .promotionId((product.getPromotion() != null) ? product.getPromotion().getId() : null)
                    .promotionName((product.getPromotion() != null) ? product.getPromotion().getName() : null)
                    .policy((product.getPromotion() != null) ? product.getPromotion().getPolicy().toString() : null)
                    .userId(product.getUser().getId())
                    .userType(product.getAuthority().toString())
                    .username(product.getUser().getName())
                    .build());
        }
        return products;
    }
}
