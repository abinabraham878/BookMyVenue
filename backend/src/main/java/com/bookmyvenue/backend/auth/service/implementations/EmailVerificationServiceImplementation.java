package com.bookmyvenue.backend.auth.service.implementations;

import com.bookmyvenue.backend.auth.entity.EmailVerificationToken;
import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.auth.repository.EmailVerificationTokenRepository;
import com.bookmyvenue.backend.auth.service.EmailVerificationServiceInterface;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import com.bookmyvenue.backend.user.entity.UserEntity;
import com.bookmyvenue.backend.user.enums.UserStatus;
import com.bookmyvenue.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImplementation implements EmailVerificationServiceInterface {

    @Value("${auth.email-verification.expiry-minutes}")
    private int tokenExpiryMinutes;
    @Value("${auth.ui-base-url}")
    private String uiBaseUrl;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createAndSendVerificationEmail(UserEntity user) {
        emailVerificationTokenRepository.deleteByUserId(user.getId());

        UUID tokenId = UUID.randomUUID();
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordService.hashPassword(rawToken);
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setId(tokenId);
        emailVerificationToken.setUserId(user.getId());
        emailVerificationToken.setTokenHash(hashedToken);
        emailVerificationToken.setExpiresAt(
                Instant.now().plus(tokenExpiryMinutes, ChronoUnit.MINUTES)
        );
        emailVerificationToken.setCreatedAt(Instant.now());
        emailVerificationTokenRepository.save(emailVerificationToken);
        String verificationUrl = uiBaseUrl + "/auth/verify-email?tokenId="+ tokenId +"&token=" + rawToken;

        log.info(
                "Verification email for {} : {}",
                user.getEmail(),
                verificationUrl
        );

    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_EMAIL_NOT_FOUND));
        createAndSendVerificationEmail(user);
    }

    @Override
    public SuccessResponse verifyEmail(UUID tokenId, String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findById(tokenId)
                .orElseThrow(()-> new BusinessException(ErrorCode.AUTH_INVALID_VERIFICATION_TOKEN));
        if (emailVerificationToken.getVerifiedAt() != null) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_VERIFIED);
        }
        if (emailVerificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.AUTH_VERIFICATION_TOKEN_EXPIRED);
        }
        boolean validToken = passwordService.matchesPassword(token, emailVerificationToken.getTokenHash());
        if (!validToken) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_VERIFICATION_TOKEN);
        }
        UserEntity user = userRepository.findById(emailVerificationToken.getUserId())
                .orElseThrow(()-> new BusinessException(ErrorCode.AUTH_INVALID_VERIFICATION_TOKEN));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        emailVerificationToken.setVerifiedAt(Instant.now());
        emailVerificationTokenRepository.save(emailVerificationToken);
        return new SuccessResponse(
                ErrorCode.AUTH_EMAIL_VERIFIED_SUCCESS.name(),
                ErrorCode.AUTH_EMAIL_VERIFIED_SUCCESS.getMessage(),
                ErrorCode.AUTH_EMAIL_VERIFIED_SUCCESS.getHttpStatus().value()
        );
    }
}