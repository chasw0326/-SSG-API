package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
