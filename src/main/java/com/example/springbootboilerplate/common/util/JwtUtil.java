package com.example.springbootboilerplate.common.util;

import com.example.springbootboilerplate.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Setter
@Getter
@Component
public class JwtUtil {
    @Value("${jwt.access-token-name}")
    private String accessTokenName;

    @Value("${jwt.refresh-token-name}")
    private String refreshTokenName;

    @Value("${jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime;

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String generateAccessToken(String userId, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .claim("type", accessTokenName)
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("type", refreshTokenName)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String getUserId(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public SimpleGrantedAuthority getRoles(String token) {
        // "role" 클레임에서 단일 권한 값 가져오기
        String role = parseToken(token).getPayload().get("role", String.class);
        // 권한을 SimpleGrantedAuthority로 변환하여 반환
        return new SimpleGrantedAuthority(role);
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (SignatureException e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.UNAUTHORIZED, "JWT signature does not match");
        } catch (ExpiredJwtException e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.UNAUTHORIZED, "JWT expired");
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 예외");
        }
    }
}
