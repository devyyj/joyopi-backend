package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.exception.CustomException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("토큰 생성")
    void generateToken() {
        String admin = jwtUtil.generateToken("admin");
        assertNotNull(admin);
    }

    @Test
    @DisplayName("토큰 파싱")
    void parseToken() {
        String admin = jwtUtil.generateToken("admin");
        String username = jwtUtil.parseToken(admin);
        assertEquals("admin", username);
    }

    @Test
    @DisplayName("만료된 토큰 파싱")
    void parseExpiredToken() {
        jwtUtil = new JwtUtil("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyB3aGlsZSBpdCBzaW5ncyBhIHR1bmUgdW5kZXIgdGhlIG1vb25saWdodCwgYW5kIG9jY2FzaW9uYWxseSBzdG9wcyB0byBlbmpveSBhIGhvdCBjdXAgb2YgY29mZmVlLg==");
        jwtUtil.setExpirationTime(0L);
        String admin = jwtUtil.generateToken("admin");
        assertThrows(CustomException.class, () -> jwtUtil.parseToken(admin));
    }

    @Test
    @DisplayName("잘못된 키로 토큰 파싱")
    void parseTokenWithWrongKey() {
        String admin = jwtUtil.generateToken("admin");
        jwtUtil = new JwtUtil("VVGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyB3aGlsZSBpdCBzaW5ncyBhIHR1bmUgdW5kZXIgdGhlIG1vb25saWdodCwgYW5kIG9jY2FzaW9uYWxseSBzdG9wcyB0byBlbmpveSBhIGhvdCBjdXAgb2YgY29mZmVlLg");
        assertThrows(CustomException.class, () -> jwtUtil.parseToken(admin));
    }
}