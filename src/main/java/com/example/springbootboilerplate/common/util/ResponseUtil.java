package com.example.springbootboilerplate.common.util;

import com.example.springbootboilerplate.common.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ApiResponseDto<Void> apiResponse = new ApiResponseDto<>(message, null);

        // JSON으로 변환 후 응답에 작성
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
