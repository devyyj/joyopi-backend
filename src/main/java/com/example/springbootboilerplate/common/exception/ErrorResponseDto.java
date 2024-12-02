package com.example.springbootboilerplate.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponseDto {
    private final int statusCode;
    private final String message;
    private final String detail;
}
