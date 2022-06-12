package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     *
     * @param userId 유저 pk
     * @return 특정유저가 등록한 프로모션들
     */
    List<Promotion> findAllByUserId(Long userId);


}
