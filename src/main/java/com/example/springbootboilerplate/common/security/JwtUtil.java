package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Setter
@Component
public class JwtUtil {
    private long expirationTime;

    private SecretKey key;

    // 생성자에서 secretKey를 설정
    public JwtUtil(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)); // Base64로 디코딩 후 SecretKey로 변환
        this.expirationTime = expirationTime;
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param username 사용자 이름 (혹은 사용자 ID)
     * @return 생성된 JWT 토큰
     */
    String generateToken(String username) {
        // 고정된 비밀키로 SecretKey 객체 생성

        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰이 유효한지 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 사용자 이름
     */
    String parseToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return claimsJws.getPayload().getSubject();
        } catch (SignatureException e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT signature does not match");
        } catch (ExpiredJwtException e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT expired");
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * JWT 토큰을 리프레시하거나 재발급합니다.
     *
     * @param token 기존 JWT 토큰
     * @return 새로운 JWT 토큰
     */
    String refreshToken(String token) {
        return null;
    }
}
