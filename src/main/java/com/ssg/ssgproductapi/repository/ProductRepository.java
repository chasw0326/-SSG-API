package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.domain.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 일반회원, 기업회원을 기준으로 현재 노출이 가능한 상품들 반환
     * @param userType 회원타입에 따라 구분
     * @param fromDate 시작시점 비교
     * @param toDate 종료시점 비교
     * @param pageable 페이징
     * @return 현재 노출 가능한 상품들
     */
    @EntityGraph(attributePaths = {"promotion", "user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Product> getAllByAuthorityAndStartedAtLessThanEqualAndEndAtGreaterThanEqual(UserType userType, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    /**
     *
     * @param promotionId 프로모션 pk
     * @return 특정 프로모션이 적용된 상품들
     */
    List<Product> getAllByPromotion_Id(Long promotionId);

    /**
     * user 즉시조인용 메서드
     * @param productId 상품 pk
     * @return user 를 조인한 product
     */
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    Product getProductById(Long productId);

    /**
     * 프로모션을 즉시조인
     * @param userId 유저 pk
     * @param pageable 페이징
     * @return 특정 유저가 등록한 상품들
     */
    @EntityGraph(attributePaths = {"promotion"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Product> getAllByUser_Id(Long userId, Pageable pageable);
}
