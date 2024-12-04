package com.example.springbootboilerplate.common.exception;

public record ErrorResponseDto(int statusCode, String message, String detail) {
}
