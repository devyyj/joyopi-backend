package com.joyopi.backend.common.exception;

import com.joyopi.backend.common.dto.ExceptionResponseDto;
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
    public ResponseEntity<ExceptionResponseDto<String>> handleCustomException(CustomException ex) {
        log.error(ex.toString());
        ExceptionResponseDto<String> responseDto = new ExceptionResponseDto<>(ex.getMessage(), ex.getDetail());
        return new ResponseEntity<>(responseDto, ex.getHttpStatus());
    }

    // 권한이 없는 경우
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponseDto<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error(ex.toString());
        ExceptionResponseDto<Void> responseDto = new ExceptionResponseDto<>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
    }

    // 존재하지 않는 경로 접근
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponseDto<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error(ex.toString());
        ExceptionResponseDto<Void> responseDto = new ExceptionResponseDto<Void>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    // 모든 예외 처리 (기타 모든 예외를 처리)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto<Void>> handleAllExceptions(Exception ex) {
        log.error(ex.toString());
        ExceptionResponseDto<Void> responseDto = new ExceptionResponseDto<Void>(ex.getMessage(), null);
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
