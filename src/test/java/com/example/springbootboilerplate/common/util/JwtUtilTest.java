package com.example.springbootboilerplate.common.util;

import com.example.springbootboilerplate.common.exception.CustomException;
import com.example.springbootboilerplate.common.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    @DisplayName("토큰 생성")
    void testGenerateToken() {
        String token = jwtUtil.generateAccessToken("admin", "ROLE_USER");
        assertNotNull(token);
    }

    @Test
    @DisplayName("토큰 파싱")
    void testGetUsername() {
        String token = jwtUtil.generateAccessToken("admin", "ROLE_USER");
        String username = jwtUtil.getUserId(token);
        assertEquals("admin", username);
    }

    @Test
    @DisplayName("만료된 토큰 파싱")
    void testGetUsernameWithExpiredToken() {
        JwtUtil expiredTokenjwtUtil = new JwtUtil(secretKey);
        expiredTokenjwtUtil.setAccessTokenExpirationTime(0L);
        String token = expiredTokenjwtUtil.generateAccessToken("admin", "ROLE_USER");
        assertThrows(CustomException.class, () -> jwtUtil.getUserId(token));
    }

    @Test
    @DisplayName("잘못된 키로 토큰 파싱")
    void testGetUsernameWithWrongKey() {
        String token = jwtUtil.generateAccessToken("admin", "ROLE_USER");
        JwtUtil wrongKeyJwtUtil = new JwtUtil(secretKey + "wrongKey");
        assertThrows(CustomException.class, () -> wrongKeyJwtUtil.getUserId(token));
    }
}