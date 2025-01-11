package com.joyopi.backend.common.dto;

/**
 * @param message    응답 메시지
 * @param data       응답 데이터
 */
public record ExceptionResponseDto<T>(String message, T data) {
}
