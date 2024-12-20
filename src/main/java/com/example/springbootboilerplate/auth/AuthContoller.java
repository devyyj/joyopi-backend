package com.example.springbootboilerplate.auth;

import com.example.springbootboilerplate.auth.dto.AuthResponseDto;
import com.example.springbootboilerplate.auth.service.AuthService;
import com.example.springbootboilerplate.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final JwtUtil jwtUtil;

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

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal String userId) {
        Cookie cookie = new Cookie(jwtUtil.getRefreshTokenName(), null);
        cookie.setPath("/");  // 쿠키 경로 설정 (애플리케이션의 루트 경로로 설정)
        cookie.setMaxAge(0);  // 쿠키 만료시간 0으로 설정하여 삭제
        // 쿠키를 응답에 추가하여 클라이언트에서 삭제되도록 함
        response.addCookie(cookie);
    }

}
