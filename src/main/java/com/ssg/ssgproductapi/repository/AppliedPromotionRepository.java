package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.AppliedPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppliedPromotionRepository extends JpaRepository<AppliedPromotion, Long> {

    AppliedPromotion getByProduct_IdAndPromotion_Id(Long productId, Long promotionId);
}
