package com.bookmyvenue.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    AUTH_EMAIL_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "Email already exists"
    ),
    AUTH_PHONE_NUMBER_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "Phone number already exists"
    ),
    AUTH_PASSWORD_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "Password and confirm password do not match"
    ),
    AUTH_INVALID_PASSWORD(
            HttpStatus.BAD_REQUEST,
            "Password does not meet security requirements"
    );

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
