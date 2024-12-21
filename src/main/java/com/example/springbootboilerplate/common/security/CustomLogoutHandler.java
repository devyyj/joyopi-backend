package com.example.springbootboilerplate.common.security;

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

    @Value("${kakao.logout-uri}")
    private String kakaoLogoutUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId; // 카카오 REST API 키
    @Value("${front-end.host}")
    private String frontEndHost; // 로그아웃 후 리다이렉트 URI

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String redirectUrl = this.kakaoLogoutUrl
                + "?client_id=" + clientId
                + "&logout_redirect_uri=" + frontEndHost + "/login?logout";

        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
