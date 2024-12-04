package com.example.springbootboilerplate.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        // 권한 정보를 문자열로 변환
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // ROLE_USER,ROLE_ADMIN 형식으로 결합

        claims.put("roles", roles);

        String token = jwtUtil.generateToken(oAuth2User.getName(), claims);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", token);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        // JSON 응답 전송
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        response.sendRedirect("/");
    }
}
