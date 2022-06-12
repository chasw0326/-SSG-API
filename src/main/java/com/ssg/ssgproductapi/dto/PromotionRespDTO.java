package com.ssg.ssgproductapi.dto;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromotionRespDTO {

    @Getter
    @Builder
    public static class MyPromotion {

        private Long id;

        private String name;

        private String description;

        private DiscountPolicy policy;

        private int discountRate;

        private LocalDateTime startedAt;

        private LocalDateTime endAt;

        private List<Long> productIds;

    }
}
