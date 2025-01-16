package com.joyopi.backend.auth.service;

import com.joyopi.backend.common.util.JwtUtil;
import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.dto.UserResponseDto;
import com.joyopi.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public String reissueAccessToken(String refreshToken) {
        String userId = jwtUtil.getUserId(refreshToken);
        UserResponseDto user = userService.getUserById(Long.parseLong(userId));
        return jwtUtil.generateAccessToken(String.valueOf(user.id()), user.role());
    }
}
