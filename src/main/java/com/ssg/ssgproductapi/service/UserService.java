package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.User;

public interface UserService {

    Long signup(User user);

    void resign(Long userId);

    void updatePassword(Long userId, String oldPw, String newPw, String checkPw);

    User getUser(Long userId);

    void updateUser(Long userId, String name);
}
