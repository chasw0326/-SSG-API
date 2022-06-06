package com.ssg.ssgproductapi.dto;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ProductDTO {

    @Getter
    @Builder
    public static class userProduct {

        private Long id;

        private String name;

        private String description;

        private int fullPrice;

        private int discountedPrice;

        private LocalDateTime createdAt;

        private LocalDateTime endAt;

        private Long promotionId;

        private Long sellerId;


    }
}
