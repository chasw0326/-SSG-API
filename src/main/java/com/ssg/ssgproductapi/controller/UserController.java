package com.ssg.ssgproductapi.controller;


import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.dto.UserDTO;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import com.ssg.ssgproductapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public User getUser(@AuthenticationPrincipal AuthUserDTO authUser) {
        return userService.getUser(authUser.getId());
    }

    @PostMapping
    public void signup(@RequestBody @Valid UserDTO.Signup signupDTO) {

        userService.signup(
                signupDTO.getEmail(),
                signupDTO.getName(),
                signupDTO.getPassword(),
                signupDTO.getUserType()
        );
    }

    @PutMapping("/withdrawal")
    public void resign(@AuthenticationPrincipal AuthUserDTO authUser) {
        userService.resign(authUser.getId());
    }

    @PutMapping
    public void updateUser(@AuthenticationPrincipal AuthUserDTO authUser,
                           @RequestBody UserDTO.Update updateDTO) {

        userService.updateUser(
                authUser.getId(),
                updateDTO.getName()
        );
    }

    @PutMapping("/password")
    public void updatePassword(@AuthenticationPrincipal AuthUserDTO authUser,
                               @RequestBody @Valid UserDTO.Password passwordDTO) {

        userService.updatePassword(
                authUser.getId(),
                passwordDTO.getOldPw(),
                passwordDTO.getNewPw(),
                passwordDTO.getCheckPw()
        );
    }
}
