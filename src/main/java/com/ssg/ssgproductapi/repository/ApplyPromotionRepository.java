package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.ApplyPromotion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyPromotionRepository extends JpaRepository<ApplyPromotion, Long> {

    ApplyPromotion getByProduct_IdAndPromotion_Id(Long productId, Long promotionId);

    @EntityGraph(attributePaths = {"promotion"}, type = EntityGraph.EntityGraphType.LOAD)
    List<ApplyPromotion> getAllByProduct_Id(Long productId);

    @Query("SELECT ap.product.id " +
            "FROM ApplyPromotion ap " +
            "WHERE ap.promotion.id =:promotionId")
    List<Long> getProductIdsByPromotionId(@Param("promotionId") Long promotionId);

    void deleteAllByPromotion_Id(Long promotionId);

    void deleteByProduct_IdAndPromotion_Id(Long productId, Long PromotionId);
}
