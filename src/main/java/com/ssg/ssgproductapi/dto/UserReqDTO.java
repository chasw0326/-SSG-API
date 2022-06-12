package com.ssg.ssgproductapi.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserReqDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signup {

        @NotBlank(message = "이메일은 필수값입니다.")
        @Email(message = "이메일형식이 아닙니다.")
        private String email;

        @NotBlank(message = "이름은 필수값 입니다.")
        private String name;

        @NotBlank(message = "비밀번호는 필수값 입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
        private String password;

        @Pattern(regexp = "(일반회원|기업회원)",
                message = "일반회원 or 기업회원을 입력해주세요")
        private String userType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Password {

        private String oldPw;

        @NotBlank(message = "비밀번호는 필수값 입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
        private String newPw;

        private String checkPw;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {

        @NotBlank(message = "이름은 필수값 입니다.")
        private String name;
    }

}
