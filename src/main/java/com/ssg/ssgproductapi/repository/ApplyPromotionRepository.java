package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.ApplyPromotion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyPromotionRepository extends JpaRepository<ApplyPromotion, Long> {

    /**
     *
     * @param productId 상품 pk
     * @param promotionId 프로모션 pk
     * @return 상품에 적용된 프로모션
     */
    ApplyPromotion getByProduct_IdAndPromotion_Id(Long productId, Long promotionId);

    /**
     *
     * @param productId 상품 pk
     * @return 해당 상품에 적용된 모든 프로모션
     */
    @EntityGraph(attributePaths = {"promotion"}, type = EntityGraph.EntityGraphType.LOAD)
    List<ApplyPromotion> getAllByProduct_Id(Long productId);

    /**
     *
     * @param promotionId 프로모션 pk
     * @return 해당 프로모션이 적용된 모든 상품들의 pk
     */
    @Query("SELECT ap.product.id " +
            "FROM ApplyPromotion ap " +
            "WHERE ap.promotion.id =:promotionId")
    List<Long> getProductIdsByPromotionId(@Param("promotionId") Long promotionId);

    /**
     * UK(productId, promotionId) 제약조건을 걸었기 때문에
     * 이미 특정 프로모션이 적용되어있는데 또 상품에 적용하려하는걸 체크하는 메서드
     * @param productId 상품 pk
     * @param promotionId 프로모션 pk
     * @return
     */
    boolean existsByProduct_IdAndPromotion_Id(Long productId, Long promotionId);

    /**
     * 특정 상품에 적용된 프로모션 제거용
     * @param productId 상품 pk
     * @param PromotionId 프로모션 pk
     */
    void deleteByProduct_IdAndPromotion_Id(Long productId, Long PromotionId);
}
