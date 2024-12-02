package com.example.springbootboilerplate.common.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class CustomException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private final String detail;

    public CustomException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
        this.detail = null;
    }

    public CustomException(HttpStatus httpStatus, String message, String detail) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
        this.detail = detail;
    }
}
