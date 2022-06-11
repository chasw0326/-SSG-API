package com.ssg.ssgproductapi.service;

import java.util.List;

public interface AppliedPromotionService {

    void deleteAppliedPromotion(Long userId, Long productId, Long promotionId);

    void dirtyCheckAppliedPromotion(Long productId);

    void applyPromotionToProduct(Long userId, Long promotionId, List<Long> productIds);
}
