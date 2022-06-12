package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.*;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ApplyPromotionRepository;
import com.ssg.ssgproductapi.repository.ProductRepository;
import com.ssg.ssgproductapi.repository.PromotionRepository;
import com.ssg.ssgproductapi.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApplyPromotionServiceImpl implements ApplyPromotionService {

    private final PromotionServiceImpl promotionService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ApplyPromotionRepository appliedPromotionRepository;
    private final PromotionRepository promotionRepository;
    private final ValidateUtil validateUtil;

    /**
     * 프로모션을 상품에 적용
     * @param userId
     * @param promotionId
     * @param productIds 상품 pk 리스트
     */
    @Override
    @Transactional
    public void applyPromotionToProduct(Long userId, Long promotionId, List<Long> productIds) {

        // 탈퇴한 회원인지 본인의 프로모션인지 체크
        this.checkAuthAndValidUser(userId, promotionId);
        Promotion promotion = promotionService.getPromotion(promotionId);

        LocalDateTime startedAt = promotion.getStartedAt();
        LocalDateTime endAt = promotion.getEndAt();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startedAt)) {
            throw new ForbiddenException("시작되지 않은 프로모션 입니다.");
        }
        if (now.isAfter(endAt)) {
            throw new ForbiddenException("종료된 프로모션 입니다.");
        }

        DiscountPolicy policy = promotion.getPolicy();
        int rate = promotion.getDiscountRate();
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("can not find product by productId: " + productId));
            if (appliedPromotionRepository.existsByProduct_IdAndPromotion_Id(productId, promotionId)){
                continue;
            }
            this.applyCheapestPromotion(product, rate, policy, promotion);
            ApplyPromotion appliedPromotion = ApplyPromotion.builder()
                    .product(product)
                    .promotion(promotion)
                    .build();

            validateUtil.validate(appliedPromotion);
            appliedPromotionRepository.save(appliedPromotion);
        }
    }

    /**
     * 상품가격의 변경이 있을때 더 적합한 프로모션이 있는지 체크하기 위한 메서드
     * @param productId
     */
    @Override
    @Transactional
    public void dirtyCheckAppliedPromotion(Long productId) {
        List<ApplyPromotion> appliedPromotions = appliedPromotionRepository.getAllByProduct_Id(productId);
        for (ApplyPromotion appliedPromotion : appliedPromotions) {
            Long appliedPromotionId = appliedPromotion.getPromotion().getId();
            if (promotionService.isValidPromotion(appliedPromotionId)) {
                Promotion promotion = promotionService.getPromotion(appliedPromotionId);
                DiscountPolicy policy = promotion.getPolicy();
                int rate = promotion.getDiscountRate();
                Product product = productRepository.getById(productId);
                this.applyCheapestPromotion(product, rate, policy, promotion);
            }
        }
    }

    /**
     * 상품에 적용된 특정 프로모션 제거 <br>
     * 프로모션이 삭제되는 것이 아니라, 상품에 적용된 프로모션을 제외
     * @param userId
     * @param productId
     * @param promotionId
     */
    @Override
    @Transactional
    public void deleteAppliedPromotion(Long userId, Long productId, Long promotionId) {
        Promotion promotion = promotionService.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        ApplyPromotion appliedPromotion = appliedPromotionRepository.getByProduct_IdAndPromotion_Id(productId, promotionId);
        appliedPromotionRepository.delete(appliedPromotion);
        // 제거 한 뒤 적합한 프로모션을 찾아서 상품에 등록
        this.dirtyCheckAppliedPromotion(productId);
    }

    /**
     * 프로모션 자체를 제거
     * @param userId
     * @param promotionId
     */
    @Override
    @Transactional
    public void deletePromotion(Long userId, Long promotionId) {
        List<Product> products = productRepository.getAllByPromotion_Id(promotionId);
        this.checkAuthAndValidUser(userId, promotionId);

        // 삭제되는 프로모션이 적용된 상품들과의 관계를 다 끊고
        // 적합한 프로모션을 찾아서 상품에 적용
        for (Product product : products) {
            product.updatePromotion(null);
            appliedPromotionRepository.deleteByProduct_IdAndPromotion_Id(product.getId(), promotionId);
            product.updateDiscountedPrice(product.getFullPrice());
            this.dirtyCheckAppliedPromotion(product.getId());
        }
        promotionRepository.deleteById(promotionId);
    }

    /**
     * 권한, 탈퇴회원 체크
     * @param userId
     * @param promotionId
     */
    private void checkAuthAndValidUser(Long userId, Long promotionId) {
        Promotion promotion = promotionService.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        User user = userService.getUser(userId);
        if (user.getUserState().equals(UserState.탈퇴)) {
            throw new ForbiddenException("탈퇴한 회원입니다. input email: " + user.getEmail());
        }
    }

    /**
     * 프로모션을 제거하거나 상품가격이 변동되면 <br>
     * 프로모션이 바뀔 수 있기 때문에 체크용 메서드
     * @param product
     * @param rate
     * @param policy
     * @param promotion
     */
    private void applyCheapestPromotion(Product product, int rate, DiscountPolicy policy, Promotion promotion) {
        int fullPrice = product.getFullPrice();
        // 정액제
        int discountedPrice = fullPrice - rate;
        // 정률제
        if (policy.equals(DiscountPolicy.FIXED)) {
            final double fixedRate = (double) (100 - rate) / 100;
            discountedPrice = (int) (fullPrice * fixedRate);
        }
        // 할인된 가격이 0보다 작으면
        if (discountedPrice <= 0) {
            return;
        }
        // 기존에 적용되고 있는 프로모션보다 할인이 더 될 경우
        if (product.getDiscountedPrice() > discountedPrice){
            product.updateDiscountedPrice(discountedPrice);
            product.updatePromotion(promotion);
        }
    }

}
