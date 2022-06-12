package com.ssg.ssgproductapi.service;


import com.google.common.base.Preconditions;
import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.domain.UserState;
import com.ssg.ssgproductapi.domain.UserType;
import com.ssg.ssgproductapi.exception.custom.AlreadyExistsException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.UserRepository;
import com.ssg.ssgproductapi.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidateUtil validateUtil;


    @Override
    @Transactional
    public void signup(String email, String name, String rawPassword, String userType) {
        Preconditions.checkNotNull(email, "email은 필수값 입니다.");
        Preconditions.checkNotNull(name, "name은 필수값 입니다.");
        Preconditions.checkNotNull(rawPassword, "password는 필수값 입니다.");
        Preconditions.checkNotNull(userType, "userType은 필수값 입니다.");

        if (userRepository.existsByEmail(email)) {
            log.warn("Email already exists input: {}", email);
            throw new AlreadyExistsException("Email already exists input: " + email);
        }
        validatePassword(rawPassword);
        String encPassword = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .email(email)
                .name(name)
                .password(encPassword)
                .build();

        user.addUserRole(UserType.일반회원);
        if (userType.equals(UserType.기업회원.toString())){
            user.addUserRole(UserType.기업회원);
        }

        validateUtil.validate(user);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resign(Long userId) {
        User user = this.getUser(userId);
        user.updateUserState(UserState.탈퇴);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPw, String newPw, String checkPw) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(oldPw, "oldPw은 필수값 입니다.");
        Preconditions.checkNotNull(newPw, "newPw은 필수값 입니다.");
        Preconditions.checkNotNull(checkPw, "checkPw은 필수값 입니다.");

        User user = this.getUser(userId);

        if (!passwordEncoder.matches(oldPw, user.getPassword())) {
            log.warn("이전 비밀번호와 다릅니다.");
            throw new InvalidArgsException("이전 비밀번호와 다릅니다.");
        }

        if (!newPw.equals(checkPw)) {
            log.warn("확인 비밀번호가 새 비밀번호와 다릅니다.");
            throw new InvalidArgsException("확인 비밀번호가 새 비밀번호와 다릅니다.");
        }

        validatePassword(newPw);
        user.updatePassword(passwordEncoder.encode(newPw));
    }

    @Override
    @Transactional
    public void updateUser(Long userId, String name) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(name, "name은 필수값 입니다.");
        User user = this.getUser(userId);
        user.updateName(name);
        validateUtil.validate(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("can not find user by userId: " + userId));
    }

    private void validatePassword(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if (!password.matches(pattern)) {
            throw new InvalidArgsException("비밀번호 형식을 확인하세요. (8~20자, 대문자, 소문자, 특수문자, 숫자 포함)");
        }
    }
}
