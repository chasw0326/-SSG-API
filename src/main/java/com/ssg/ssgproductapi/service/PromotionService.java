package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.dto.PromotionRespDTO;

import java.util.List;

public interface PromotionService {

    List<PromotionRespDTO.MyPromotion> getMyPromotions(Long userId);

    void registerPromotion(Long userId, String name, String description, String policy, Integer discountRate, String start, String end);

    Promotion getPromotion(Long promotionId);

    boolean isValidPromotion(Long promotionId);

}
