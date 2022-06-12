package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.dto.PromotionRespDTO;

import java.util.List;

public interface PromotionService {

    boolean isValidPromotion(Long promotionId);

    Promotion getPromotion(Long promotionId);

    void registerPromotion(Long userId, String name, String description, String policy, Integer discountRate, String start, String end);

    // DEPRECATED
//    void updatePromotion(Long userId, Long promotionId, String name, String description, String policy, Integer discountRate, String start, String end);

    List<PromotionRespDTO.MyPromotion> getMyPromotions(Long userId);

}
