package com.example.springbootboilerplate;

import com.example.springbootboilerplate.common.exception.ErrorResponseDto;
import com.example.springbootboilerplate.common.security.JwtUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DefaultController {

    private final JwtUtil jwtUtil;

    @GetMapping
    public String root() {
        return "Hello World";
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
// "Bearer "를 제외한 리프레시 토큰만 추출
        String refreshToken = authorizationHeader.replace("Bearer ", "");

        // 새로운 액세스 토큰 발급
        String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

        // 새로운 액세스 토큰을 반환
        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }
}
