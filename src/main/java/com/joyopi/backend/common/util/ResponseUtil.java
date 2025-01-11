package com.joyopi.backend.common.util;

import com.joyopi.backend.common.dto.ExceptionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 상태 코드, 메시지, 데이터를 받아 응답을 작성하는 유틸리티 메서드
     *
     * @param response   HttpServletResponse 객체
     * @param statusCode HTTP 상태 코드
     * @param message    응답 메시지
     * @throws IOException JSON 변환 및 응답 작성 중 오류 발생 시
     */
    public static void writeJsonResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ApiResponseDto 생성
        ExceptionResponseDto<Void> apiResponse = new ExceptionResponseDto<>(message, null);

        // JSON으로 변환 후 응답에 작성
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    /**
     * 지정된 이름의 쿠키를 삭제하는 유틸리티 메서드
     *
     * @param response   HttpServletResponse 객체
     * @param cookieName 삭제할 쿠키의 이름
     */
    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null); // 지정된 이름의 쿠키 삭제
        cookie.setPath("/"); // 애플리케이션의 루트 경로에 적용
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 쿠키 삭제
        cookie.setHttpOnly(true); // 보안 설정
        response.addCookie(cookie); // 응답에 추가하여 클라이언트에서 삭제
    }
}
