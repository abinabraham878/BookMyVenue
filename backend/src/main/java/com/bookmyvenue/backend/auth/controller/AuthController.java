package com.bookmyvenue.backend.auth.controller;

import com.bookmyvenue.backend.auth.dto.request.RegisterRequest;
import com.bookmyvenue.backend.auth.service.EmailVerificationServiceInterface;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.user.enums.Role;
import com.bookmyvenue.backend.user.service.interfaces.RegistrationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegistrationServiceInterface registrationService;
    private final EmailVerificationServiceInterface  emailVerificationService;

    @PostMapping("/register")
    public SuccessResponse registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return registrationService.registerUser(registerRequest, Role.USER);
    }

    @PostMapping("/register-owner")
    public SuccessResponse registerOwner(@Valid @RequestBody RegisterRequest registerRequest) {
        return registrationService.registerUser(registerRequest, Role.VENUE_OWNER);
    }

    @GetMapping("/verify-email")
    public SuccessResponse verifyEmail(@RequestParam UUID tokenId, @RequestParam String token) {
        return emailVerificationService.verifyEmail(tokenId, token);
    }

    @PostMapping("/resend-verification")
    public SuccessResponse resendVerificationToken(@RequestParam String email) {
        return emailVerificationService.resendVerificationEmail(email);
    }
}
