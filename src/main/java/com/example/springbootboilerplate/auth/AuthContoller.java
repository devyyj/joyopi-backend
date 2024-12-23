package com.example.springbootboilerplate.auth;

import com.example.springbootboilerplate.auth.dto.AuthResponseDto;
import com.example.springbootboilerplate.auth.service.AuthService;
import com.example.springbootboilerplate.common.exception.CustomException;
import com.example.springbootboilerplate.common.util.JwtUtil;
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
    public void deleteRefreshToken(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal String userId) {
        Cookie cookie = new Cookie(jwtUtil.getRefreshTokenName(), null);
        cookie.setPath("/");  // 쿠키 경로 설정 (애플리케이션의 루트 경로로 설정)
        cookie.setMaxAge(0);  // 쿠키 만료시간 0으로 설정하여 삭제
        // 쿠키를 응답에 추가하여 클라이언트에서 삭제되도록 함
        response.addCookie(cookie);
    }

}
