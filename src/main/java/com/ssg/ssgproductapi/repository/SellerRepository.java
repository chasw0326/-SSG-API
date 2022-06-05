package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
