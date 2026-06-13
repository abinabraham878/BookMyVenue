package com.bookmyvenue.backend.common.dto;

public record SuccessResponse(
        String statusCode,
        String message,
        int status
) {
}
