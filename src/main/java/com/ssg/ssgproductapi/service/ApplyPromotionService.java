package com.ssg.ssgproductapi.service;

import java.util.List;

public interface ApplyPromotionService {

    void applyPromotionToProduct(Long userId, Long promotionId, List<Long> productIds);

    void dirtyCheckAppliedPromotion(Long productId);

    void deleteAppliedPromotion(Long userId, Long productId, Long promotionId);

    void deletePromotion(Long userId, Long promotionId);
}
