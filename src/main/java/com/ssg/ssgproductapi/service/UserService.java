package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.User;

public interface UserService {

    void signup(String email, String name, String rawPassword, String userType);

    void resign(Long userId);

    User getUser(Long userId);

    void updateUser(Long userId, String name);

    void updatePassword(Long userId, String oldPw, String newPw, String checkPw);

}
