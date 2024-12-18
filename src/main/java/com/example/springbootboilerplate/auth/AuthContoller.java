package com.example.springbootboilerplate.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthContoller {

    @Value("${jwt.refresh-token-name}")
    private String refreshTokenName;

    private final AuthService authService;

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshTokenName.equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    // 리프레시 토큰을 사용하여 액세스 토큰 재발급 처리
                    String accessToken = authService.reissueAccessToken(refreshToken);
                    AuthResponseDto responseDto = new AuthResponseDto(accessToken);
                    return new ResponseEntity<>(responseDto, HttpStatus.OK);
                }
            }
        }
        // 쿠키에 리프레시 토큰 없을 경우
        AuthResponseDto responseDto = new AuthResponseDto(null);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

}
