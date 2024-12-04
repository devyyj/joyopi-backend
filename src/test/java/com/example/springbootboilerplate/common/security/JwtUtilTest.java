package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Test
    @DisplayName("토큰 생성")
    void testGenerateToken() {
        String token = jwtUtil.generateToken("admin");
        assertNotNull(token);
    }

    @Test
    @DisplayName("토큰 파싱")
    void testGetUsername() {
        String token = jwtUtil.generateToken("admin");
        String username = jwtUtil.getUsername(token);
        assertEquals("admin", username);
    }

    @Test
    @DisplayName("만료된 토큰 파싱")
    void testGetUsernameWithExpiredToken() {
        JwtUtil expiredTokenjwtUtil = new JwtUtil(secretKey, 0L);
        String token = expiredTokenjwtUtil.generateToken("admin");
        assertThrows(CustomException.class, () -> jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("잘못된 키로 토큰 파싱")
    void testGetUsernameWithWrongKey() {
        String token = jwtUtil.generateToken("admin");
        JwtUtil wrongKeyJwtUtil = new JwtUtil(secretKey + "wrongKey", expirationTime);
        assertThrows(CustomException.class, () -> wrongKeyJwtUtil.getUsername(token));
    }
}