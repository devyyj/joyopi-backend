package com.example.springbootboilerplate.common.security;

import com.example.springbootboilerplate.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Component
public class JwtUtil {
    private final Long expirationTime;

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secretKey
            , @Value("${jwt.expiration}") Long expirationTime) {
        this.expirationTime = expirationTime;
        // Base64 URL 디코딩 방식으로 key 생성
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Collection<? extends GrantedAuthority> getRoles(String token) {
        // "roles" 클레임에서 권한 문자열 가져오기
        String roles = getClaims(token).get("roles", String.class);

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList(); // 권한이 없으면 빈 리스트 반환
        }

        // 권한 문자열을 GrantedAuthority로 변환
        return Arrays.stream(roles.split(",")) // ROLE_USER,ROLE_ADMIN 형식 분리
                .map(SimpleGrantedAuthority::new) // 권한 문자열을 GrantedAuthority로 매핑
                .collect(Collectors.toList());
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
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
