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

    @EntityGraph(attributePaths = {"promotion", "user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Product> getAllByAuthorityAndStartedAtGreaterThanEqualAndEndAtLessThanEqual(UserType userType, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    List<Product> getAllByPromotion_Id(Long promotionId);

    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    Product getProductById(Long productId);
}
