package com.ssg.ssgproductapi.service;


import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.domain.UserState;
import com.ssg.ssgproductapi.domain.UserType;
import com.ssg.ssgproductapi.exception.custom.AlreadyExistsException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.UserRepository;
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

    @Override
    @Transactional
    public Long signup(User user) {
        final String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            log.warn("Email already exists input: {}", email);
            throw new AlreadyExistsException("Email already exists input: " + email);
        }
        String rawPassword = user.getPassword();
        validatePassword(rawPassword);

        String encPassword = passwordEncoder.encode(rawPassword);
        user.updatePassword(encPassword);
        user.addUserRole(UserType.일반);
        userRepository.save(user);
        return user.getId();
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
        User user = this.getUser(userId);
        user.updateName(name);
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
            throw new InvalidArgsException("비밀번호 형식을 확인하세요.");
        }
    }
}