package com.ssg.ssgproductapi.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductReqDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {

        @NotBlank(message = "상품명은 필수값 입니다.")
        private String name;

        @NotBlank(message = "상품 설명은 필수값 입니다.")
        private String description;

        @Positive(message = "가격은 양수여야 합니다.")
        private int fullPrice;

        @NotNull
        @Pattern(regexp = "(일반회원|기업회원)",
                message = "일반회원 or 기업회원을 입력해주세요")
        private String authority;

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
    public static class Update {

        @Builder.Default
        private String authority = null;

        @Builder.Default
        private String name = null;

        @Builder.Default
        private String description = null;

        @Builder.Default
        private Integer price = null;

        @Builder.Default
        private String start = null;

        @Builder.Default
        private String end = null;
    }
}
