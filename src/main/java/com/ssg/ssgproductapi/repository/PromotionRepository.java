package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findAllByUserId(Long userId);

}
