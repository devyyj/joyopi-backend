package com.joyopi.backend.user.controller;

import com.joyopi.backend.user.dto.UserRequestDto;
import com.joyopi.backend.user.dto.UserResponseDto;
import com.joyopi.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal Long userId) {
        UserResponseDto userResponseDto = userService.getUserById(userId);
        return ResponseEntity.ok(userResponseDto);  // DTO 반환
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> patchCurrentUser(@AuthenticationPrincipal Long userId, @RequestBody UserRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(updatedUser);  // DTO 반환
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
    }
}
