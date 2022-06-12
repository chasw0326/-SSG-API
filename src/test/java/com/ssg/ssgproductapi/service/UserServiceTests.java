package com.ssg.ssgproductapi.service;


import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.domain.UserState;
import com.ssg.ssgproductapi.exception.custom.AlreadyExistsException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @DisplayName("중복된 이메일로 가입")
    @Test
    void Should_ThrowException_WhenDuplicatedEmail() {
        Throwable ex = assertThrows(AlreadyExistsException.class, () ->
                userService.signup("cha@ssg.com", "차선욱", "abcABC123!@#", "일반회원")
        );
        assertEquals("Email already exists input: cha@ssg.com", ex.getMessage());
    }

    @DisplayName("비밀번호 강도 미충족")
    @Test
    void Should_ThrowException_WhenWeakPassword() {
        Throwable ex = assertThrows(InvalidArgsException.class, () ->
                userService.signup("normal@ssg.com", "차선욱", "abcABC123", "일반회원")
        );
        assertEquals("비밀번호 형식을 확인하세요. (8~20자, 대문자, 소문자, 특수문자, 숫자 포함)", ex.getMessage());
    }

    @DisplayName("정상적으로 탈퇴되는가")
    @Test
    void Should_Resign_WhenNormalRequest() {
        String email = "resign@ssg.com";
        userService.signup(email, "차선욱", "abcABC123!@#", "일반회원");
        User user = userRepository.findByEmail(email).get();
        userService.resign(user.getId());
        assertEquals(user.getUserState(), UserState.탈퇴);
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트들")
    class PasswordTest {

        @DisplayName("새로운 비밀번호 강도가 약할때")
        @Test
        void Should_ThrowException_WhenNewPasswordIsWeak() {
            assertThrows(InvalidArgsException.class, () ->
                    // commandLineRunner에 있는 값
                    userService.updatePassword(1L, "ssgSSG123!@#", "abc", "abc")
            );
        }

        @DisplayName("newPw와 checkPw가 다를 때")
        @Test
        void Should_ThrowException_WhenNewPwAndCheckPwIsDiff() {
            assertThrows(InvalidArgsException.class, () ->
                    // commandLineRunner에 있는 값
                    userService.updatePassword(1L, "ssgSSG123!@#", "@ssgSSG123!@#", "#ssgSSG123!@#")
            );
        }

        @DisplayName("정상적인 비밀번호 변경")
        @Test
        void Should_ChangePassword() {
            String newPassword = "SSGssgSSG123!@#";
            userService.updatePassword(1L, "ssgSSG123!@#", newPassword, newPassword);
            User user = userRepository.getById(1L);
            assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
        }
    }

    @DisplayName("정상적인 이름변경")
    @Test
    void Should_ChangeName() {
        User user = userRepository.getById(1L);
        userService.updateUser(1L, "새로운 이름");
        assertEquals("새로운 이름", user.getName());
    }

}
