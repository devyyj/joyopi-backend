package com.example.springbootboilerplate.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes; // 사용자 정보 저장
    private final Collection<? extends GrantedAuthority> authorities; // 권한 정보
    // JWT 토큰 반환
    @Getter
    private final String jwtToken; // JWT 토큰 (필요한 경우)

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("id")); // 'name' 속성을 기본값으로 사용
    }

}
