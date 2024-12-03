package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.exception.CustomException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyB3aGlsZSBpdCBzaW5ncyBhIHR1bmUgdW5kZXIgdGhlIG1vb25saWdodCwgYW5kIG9jY2FzaW9uYWxseSBzdG9wcyB0byBlbmpveSBhIGhvdCBjdXAgb2YgY29mZmVlLg=="
                , 3600000
        );
    }

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
        jwtUtil.setExpirationTime(0);
        String admin = jwtUtil.generateToken("admin");
        assertThrows(CustomException.class, () -> jwtUtil.parseToken(admin));
    }

    @Test
    @DisplayName("잘못된 키로 토큰 파싱")
    void parseTokenWithWrongKey() {
        String admin = jwtUtil.generateToken("admin");
        jwtUtil.setKey(Keys.hmacShaKeyFor("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z 0 1 2 3 4 5 6 7 8 9".getBytes(StandardCharsets.UTF_8)));
        assertThrows(CustomException.class, () -> jwtUtil.parseToken(admin));
    }
}