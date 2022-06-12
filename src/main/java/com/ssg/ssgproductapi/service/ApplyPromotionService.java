package com.ssg.ssgproductapi.service;

import java.util.List;

public interface ApplyPromotionService {

    void deleteAppliedPromotion(Long userId, Long productId, Long promotionId);

    void dirtyCheckAppliedPromotion(Long productId);

    void applyPromotionToProduct(Long userId, Long promotionId, List<Long> productIds);

    void deletePromotion(Long userId, Long promotionId);
}
