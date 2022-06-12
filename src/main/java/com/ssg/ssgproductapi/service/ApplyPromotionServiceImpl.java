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
    private final ApplyPromotionRepository appliedPromotionRepository;
    private final PromotionRepository promotionRepository;
    private final ValidateUtil validateUtil;

    @Override
    @Transactional
    public void applyPromotionToProduct(Long userId, Long promotionId, List<Long> productIds) {
        Promotion promotion = promotionService.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

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
            Product product = productRepository.getById(productId);
            this.applyCheapestPromotion(product, rate, policy, promotion);
            ApplyPromotion appliedPromotion = ApplyPromotion.builder()
                    .product(product)
                    .promotion(promotion)
                    .build();

            validateUtil.validate(appliedPromotion);
            appliedPromotionRepository.save(appliedPromotion);
        }
    }

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

    @Override
    @Transactional
    public void deleteAppliedPromotion(Long userId, Long productId, Long promotionId) {
        Promotion promotion = promotionService.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        ApplyPromotion appliedPromotion = appliedPromotionRepository.getByProduct_IdAndPromotion_Id(productId, promotionId);
        appliedPromotionRepository.delete(appliedPromotion);
        this.dirtyCheckAppliedPromotion(productId);
    }

    @Override
    @Transactional
    public void deletePromotion(Long userId, Long promotionId) {
        List<Product> products = productRepository.getAllByPromotion_Id(promotionId);
        Promotion promotion = promotionService.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        for (Product product : products) {
            product.updatePromotion(null);
            appliedPromotionRepository.deleteByProduct_IdAndPromotion_Id(product.getId(), promotionId);
            product.updateDiscountedPrice(product.getFullPrice());
            this.dirtyCheckAppliedPromotion(product.getId());
        }
        promotionRepository.deleteById(promotionId);
    }


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