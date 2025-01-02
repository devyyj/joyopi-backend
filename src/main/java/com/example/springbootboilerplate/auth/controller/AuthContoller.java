package com.example.springbootboilerplate.auth.controller;

import com.example.springbootboilerplate.auth.dto.AuthResponseDto;
import com.example.springbootboilerplate.auth.service.AuthService;
import com.example.springbootboilerplate.common.exception.CustomException;
import com.example.springbootboilerplate.common.util.JwtUtil;
import com.example.springbootboilerplate.common.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthContoller {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @GetMapping("/reissue-token")
    public ResponseEntity<AuthResponseDto> reissueToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (jwtUtil.getRefreshTokenName().equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    // 리프레시 토큰을 사용하여 액세스 토큰 재발급 처리
                    String accessToken = authService.reissueAccessToken(refreshToken);
                    AuthResponseDto responseDto = new AuthResponseDto(accessToken);
                    return new ResponseEntity<>(responseDto, HttpStatus.OK);
                }
            }
        }
        throw new CustomException(HttpStatus.BAD_REQUEST, "Refresh token is missing.");
    }

    @DeleteMapping("/refresh-token")
    public void deleteRefreshToken(HttpServletResponse response) {
        ResponseUtil.deleteCookie(response, jwtUtil.getRefreshTokenName());
    }
}
