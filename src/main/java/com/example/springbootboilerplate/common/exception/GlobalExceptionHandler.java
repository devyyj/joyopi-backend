package com.example.springbootboilerplate.common.exception;

import com.example.springbootboilerplate.common.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto<String>> handleCustomException(CustomException ex) {
        log.error(ex.toString());
        ApiResponseDto<String> responseDto = new ApiResponseDto<>(ex.getMessage(), ex.getDetail());
        return new ResponseEntity<>(responseDto, ex.getHttpStatus());
    }

    // 권한이 없는 경우
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error(ex.toString());
        ApiResponseDto<Void> responseDto = new ApiResponseDto<>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
    }

    // 존재하지 않는 경로 접근
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error(ex.toString());
        ApiResponseDto<Void> responseDto = new ApiResponseDto<Void>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    // 모든 예외 처리 (기타 모든 예외를 처리)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAllExceptions(Exception ex) {
        log.error(ex.toString());
        ApiResponseDto<Void> responseDto = new ApiResponseDto<Void>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
