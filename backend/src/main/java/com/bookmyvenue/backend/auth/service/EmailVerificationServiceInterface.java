package com.bookmyvenue.backend.auth.service;

import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.user.entity.UserEntity;

import java.util.UUID;

public interface EmailVerificationServiceInterface {

    void createAndSendVerificationEmail(UserEntity user);

    void resendVerificationEmail(String email);

    SuccessResponse verifyEmail(UUID tokenId, String token);
}
