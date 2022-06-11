package com.ssg.ssgproductapi.dto;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PromotionDTO {

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

    }
}
