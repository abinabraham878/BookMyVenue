package com.bookmyvenue.backend.auth.service;

import com.bookmyvenue.backend.user.entity.UserEntity;

public interface EmailVerificationServiceInterface {
    void createAndSendVerificationEmail(UserEntity user);

    void resendVerificationEmail(UserEntity user);
}
