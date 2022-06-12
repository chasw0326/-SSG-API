package com.ssg.ssgproductapi.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRespDTO {

    @Getter
    @Builder
    public static class UserProduct {

        private Long id;

        private String name;

        private String description;

        private int fullPrice;

        private int discountedPrice;

        private LocalDateTime createdAt;

        private LocalDateTime startAt;

        private LocalDateTime endAt;

        private Long promotionId;

        private String promotionName;

        private String policy;

        private Long userId;

        private String username;

        private String userType;
    }

    @Getter
    @Builder
    public static class Product {

        private String name;

        private String description;

        private int fullPrice;

        private int discountedPrice;

        private LocalDateTime startedAt;

        private LocalDateTime endAt;

        private String username;

    }
}
