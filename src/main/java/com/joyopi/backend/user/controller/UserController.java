package com.joyopi.backend.user.controller;

import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.dto.UserResponseDto;
import com.joyopi.backend.user.service.UserMapper;
import com.joyopi.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal Long userId) {
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toResponseDto(userById));
    }

    @DeleteMapping("/me")
    public void deleteUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
    }

}
