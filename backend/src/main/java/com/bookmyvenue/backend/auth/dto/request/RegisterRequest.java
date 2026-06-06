package com.bookmyvenue.backend.auth.dto.request;

public record RegisterRequest(
        String email,
        String password,
        String confirmPassword,
        String firstName,
        String lastName,
        String countryCode,
        String phoneNumber
) {
}
