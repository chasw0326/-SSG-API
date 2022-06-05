package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
