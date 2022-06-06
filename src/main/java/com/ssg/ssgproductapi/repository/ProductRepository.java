package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> getAllByAuthorityAndStartedAtGreaterThanEqualAndEndAtLessThanEqual(UserType userType, LocalDateTime fromDate, LocalDateTime toDate);

    List<Product> getAllByStartedAtGreaterThanEqualAndEndAtLessThanEqual(LocalDateTime fromDate, LocalDateTime toDate);
}
