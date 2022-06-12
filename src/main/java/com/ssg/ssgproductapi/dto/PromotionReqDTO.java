package com.ssg.ssgproductapi.dto;


import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromotionReqDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {

        @NotBlank(message = "프로모션명은 필수값 입니다.")
        private String name;

        @NotBlank(message = "프로모션명은 필수값 입니다.")
        private String description;

        @Pattern(regexp = "(FLAT|FIXED)",
                message = "정액제는 FLAT, 정률제는 FIXED를 입력해주세요")
        private String policy;

        @Positive(message = "할인율은 양수여야 합니다.")
        private Integer discountRate;

        @NotNull
        @Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "YYYY-MM-DD HH:MM:SS 형식으로 입력해주세요 ex) 2022-06-13 13:00:00")
        private String start;

        @NotNull
        @Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "YYYY-MM-DD HH:MM:SS 형식으로 입력해주세요 ex) 2022-06-13 13:00:00")
        private String end;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Apply {

        private List<Long> productIds;
    }
}
