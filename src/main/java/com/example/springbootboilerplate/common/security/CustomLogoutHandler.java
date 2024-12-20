package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;

    @Value("${kakao.logout-uri}")
    private String KAKAO_LOGOUT_URL;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String REST_API_KEY; // 카카오 REST API 키
    @Value("${kakao.logout-redirect-uri}")
    private String LOGOUT_REDIRECT_URI; // 로그아웃 후 리다이렉트 URI

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String kakaoLogoutUrl = KAKAO_LOGOUT_URL
                + "?client_id=" + REST_API_KEY
                + "&logout_redirect_uri=" + LOGOUT_REDIRECT_URI;

        try {
            response.sendRedirect(kakaoLogoutUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//
//        // 세션 무효화
//        if (authentication != null) {
//            request.getSession().invalidate();
//        }
//
//        // 리프레시 토큰 쿠키 삭제
//        Cookie refreshTokenCookie = new Cookie(jwtUtil.getRefreshTokenName(), null);
//        refreshTokenCookie.setPath("/"); // 적용 경로 설정
//        refreshTokenCookie.setMaxAge(0); // 쿠키 삭제를 위해 만료 시간 0으로 설정
    }
}
