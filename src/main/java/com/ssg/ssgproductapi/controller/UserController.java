package com.ssg.ssgproductapi.controller;


import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.dto.UserReqDTO;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import com.ssg.ssgproductapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 컨트롤러는 따로 깃허브 위키에 자세하게 문서화 하였습니다.
 */
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
    public void signup(@RequestBody @Valid UserReqDTO.Signup signupDTO) {

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
                           @RequestBody UserReqDTO.Update updateDTO) {

        userService.updateUser(
                authUser.getId(),
                updateDTO.getName()
        );
    }

    @PutMapping("/password")
    public void updatePassword(@AuthenticationPrincipal AuthUserDTO authUser,
                               @RequestBody @Valid UserReqDTO.Password passwordDTO) {

        userService.updatePassword(
                authUser.getId(),
                passwordDTO.getOldPw(),
                passwordDTO.getNewPw(),
                passwordDTO.getCheckPw()
        );
    }
}
