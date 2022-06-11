package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import com.ssg.ssgproductapi.domain.Promotion;

import java.util.List;

public interface PromotionService {

    boolean isValidPromotion(Long promotionId);

    Promotion getPromotion(Long promotionId);

    void registerPromotion(Long userId, String name, String description, String policy, int discountRate, String start, String end);

    void updatePromotion(Long userId, Long promotionId, String name, String description, String policy, int discountRate, String start, String end);

    List<Promotion> getMyPromotions(Long userId);
}
