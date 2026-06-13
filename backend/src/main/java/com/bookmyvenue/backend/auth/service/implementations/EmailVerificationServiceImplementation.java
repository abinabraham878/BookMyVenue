package com.bookmyvenue.backend.auth.service.implementations;

import com.bookmyvenue.backend.auth.service.EmailVerificationServiceInterface;
import com.bookmyvenue.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImplementation implements EmailVerificationServiceInterface {

    @Override
    public void createAndSendVerificationEmail(UserEntity user) {

    }

    @Override
    public void resendVerificationEmail(UserEntity user) {

    }
}
