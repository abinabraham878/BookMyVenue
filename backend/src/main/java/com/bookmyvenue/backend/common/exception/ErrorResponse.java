package com.bookmyvenue.backend.common.exception;

public record ErrorResponse(
        String errorCode,
        String message,
        int status,
        String path
) {
}
