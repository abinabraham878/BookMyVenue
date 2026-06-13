package com.bookmyvenue.backend.UserTest;

import com.bookmyvenue.backend.auth.entity.EmailVerificationToken;
import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.auth.repository.EmailVerificationTokenRepository;
import com.bookmyvenue.backend.auth.service.implementations.EmailVerificationServiceImplementation;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import com.bookmyvenue.backend.user.entity.UserEntity;
import com.bookmyvenue.backend.user.enums.UserStatus;
import com.bookmyvenue.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceImplementationTest {
    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationServiceImplementation service;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                service,
                "tokenExpiryMinutes",
                5
        );

        ReflectionTestUtils.setField(
                service,
                "uiBaseUrl",
                "http://localhost:3000"
        );

        user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("john@gmail.com");
    }

    @Test
    @DisplayName("Should delete old tokens before creating new one")
    void shouldDeleteOldTokensBeforeCreatingNewOne() {

        when(passwordService.hashPassword(any())).thenReturn("hashed-token");
        service.createAndSendVerificationEmail(user);
        verify(tokenRepository).deleteByUserId(user.getId());
    }

    @Test
    @DisplayName("Should create verification token")
    void shouldCreateVerificationToken() {

        when(passwordService.hashPassword(any())).thenReturn("hashed-token");
        service.createAndSendVerificationEmail(user);
        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    @DisplayName("Should hash verification token")
    void shouldHashVerificationToken() {

        when(passwordService.hashPassword(any())).thenReturn("hashed-token");
        service.createAndSendVerificationEmail(user);

        ArgumentCaptor<EmailVerificationToken> captor =ArgumentCaptor.forClass(
                        EmailVerificationToken.class
                );

        verify(tokenRepository).save(captor.capture());

        assertThat(captor.getValue().getTokenHash()).isEqualTo("hashed-token");
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() {

        UUID tokenId = UUID.randomUUID();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setId(tokenId);
        token.setUserId(user.getId());
        token.setTokenHash("hashed-token");
        token.setExpiresAt(Instant.now().plusSeconds(300));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(passwordService.matchesPassword(any(),any())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        SuccessResponse response =service.verifyEmail(tokenId,"raw-token");

        assertThat(response.statusCode()).isEqualTo(ErrorCode.AUTH_EMAIL_VERIFIED_SUCCESS.name());

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    @DisplayName("Should throw exception when token expired")
    void shouldThrowExceptionWhenTokenExpired() {

        UUID tokenId = UUID.randomUUID();

        EmailVerificationToken token = new EmailVerificationToken();

        token.setId(tokenId);
        token.setExpiresAt(Instant.now().minusSeconds(60));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        BusinessException exception =catchThrowableOfType(
                        () -> service.verifyEmail(tokenId,"token"),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_VERIFICATION_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Should throw exception when token does not exist")
    void shouldThrowExceptionWhenTokenDoesNotExist() {

        UUID tokenId = UUID.randomUUID();

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.empty());

        BusinessException exception = catchThrowableOfType(
                        () -> service.verifyEmail(tokenId, "token"),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("Should throw exception when token already verified")
    void shouldThrowExceptionWhenTokenAlreadyVerified() {

        UUID tokenId = UUID.randomUUID();

        EmailVerificationToken token = new EmailVerificationToken();

        token.setId(tokenId);
        token.setVerifiedAt(Instant.now());

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        BusinessException exception = catchThrowableOfType(
                        () -> service.verifyEmail(tokenId, "token"),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_VERIFIED);
    }

    @Test
    @DisplayName("Should throw exception when token is invalid")
    void shouldThrowExceptionWhenTokenIsInvalid() {

        UUID tokenId = UUID.randomUUID();

        EmailVerificationToken token = new EmailVerificationToken();

        token.setId(tokenId);
        token.setUserId(user.getId());
        token.setTokenHash("hashed-token");
        token.setExpiresAt(Instant.now().plusSeconds(300));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        when(passwordService.matchesPassword(any(), any())).thenReturn(false);

        BusinessException exception =catchThrowableOfType(
                        () -> service.verifyEmail(tokenId,"wrong-token"),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("Should resend verification email")
    void shouldResendVerificationEmail() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(passwordService.hashPassword(any())).thenReturn("hashed-token");

        service.resendVerificationEmail(user.getEmail());

        verify(tokenRepository).deleteByUserId(user.getId());

        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }
}
