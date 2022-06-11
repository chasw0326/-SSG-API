package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.AppliedPromotion;
import com.ssg.ssgproductapi.domain.Promotion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppliedPromotionRepository extends JpaRepository<AppliedPromotion, Long> {

    AppliedPromotion getByProduct_IdAndPromotion_Id(Long productId, Long promotionId);

    @EntityGraph(attributePaths = {"promotion"}, type = EntityGraph.EntityGraphType.LOAD)
    List<AppliedPromotion> getAllByProduct_Id(Long productId);
}
