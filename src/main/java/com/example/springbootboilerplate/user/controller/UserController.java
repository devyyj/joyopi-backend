package com.example.springbootboilerplate.user.controller;

import com.example.springbootboilerplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/me")
    public void deleteUser(@AuthenticationPrincipal String userId) {
        userService.deleteUser(Long.parseLong(userId));
    }

}
