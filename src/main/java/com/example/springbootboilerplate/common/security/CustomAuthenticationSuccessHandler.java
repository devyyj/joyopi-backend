package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${common.front-end.host}")
    private String frontEndHost;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 권한 가져오기
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse(null);
        // user id 가져오기
        String userId = oAuth2User.getAttribute("userId");
        // 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(userId, role);
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        // 리프레시 토큰 쿠키에 설정
        Cookie refreshTokenCookie = getRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);
        // 리다이렉트 URL 구성
        String redirectUrl = constructRedirectUrl(frontEndHost, accessToken);
        // 리다이렉트 수행
        response.sendRedirect(redirectUrl);
    }

    private String constructRedirectUrl(String baseRedirectUrl, String accessToken) {
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        return baseRedirectUrl + "/login" + "?access_token=" + encodedAccessToken;
    }

    private Cookie getRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(jwtUtil.getRefreshTokenName(), refreshToken);
        cookie.setPath("/");
        // 쿠키의 만료 시간 설정 (refreshTokenExpirationTime과 같게 설정)
//        cookie.setMaxAge(jwtUtil.getRefreshTokenExpirationTime().intValue() / 1000);
        cookie.setMaxAge(-1);
        cookie.setHttpOnly(true);
        // 쿠키가 동일 사이트에서만 유효하도록 설정, "SameSite" 속성 수동으로 추가
        cookie.setAttribute("SameSite", "Strict");  // 또는 "Lax"
        // 쿠키가 HTTPS에서만 전송되도록 설정 (보안)
//        cookie.setSecure(true);  // HTTPS 연결일 때만 사용
        return cookie;
    }
}
