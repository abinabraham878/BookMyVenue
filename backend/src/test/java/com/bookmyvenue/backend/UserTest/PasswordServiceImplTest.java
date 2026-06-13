package com.bookmyvenue.backend.UserTest;

import com.bookmyvenue.backend.auth.service.implementations.PasswordServiceImplementation;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

public class PasswordServiceImplTest {

    private PasswordServiceImplementation passwordServiceImplementation;

    @BeforeEach
    public void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        passwordServiceImplementation = new PasswordServiceImplementation(passwordEncoder);
    }

    @Test
    @DisplayName("Should validate a valid password")
    public void shouldValidateValidPassword() {
        assertThatCode(() -> passwordServiceImplementation.validatePassword(
                "Password@123",
                "Password@123"
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception when passwords does not match")
    public void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        BusinessException exception = catchThrowableOfType(
                () -> passwordServiceImplementation.validatePassword(
                        "Password@123",
                        "Password@456"
                ),
                BusinessException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_PASSWORD_MISMATCH);
    }

    @Test
    @DisplayName("Should throw exception for invalid password")
    public void shouldThrowExceptionForInvalidPassword() {
        BusinessException exception = catchThrowableOfType(
                () -> passwordServiceImplementation.validatePassword(
                        "password",
                        "password"
                ),
                BusinessException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_PASSWORD);
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    public void shouldThrowExceptionWhenPasswordIsNull() {
        BusinessException exception = catchThrowableOfType(
                () -> passwordServiceImplementation.validatePassword(
                        null,
                        "password"
                ),
                BusinessException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_PASSWORD);
    }

    @Test
    @DisplayName("Should throw exception when confirm password is null")
    public void shouldThrowExceptionWhenConfirmPasswordIsNull() {
        BusinessException exception = catchThrowableOfType(
                () -> passwordServiceImplementation.validatePassword(
                        "password",
                        null
                ),
                BusinessException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_PASSWORD);
    }

    @Test
    @DisplayName("Should hash password")
    public void shouldHashPassword() {
        String hash = passwordServiceImplementation.hashPassword("password");
        assertThat(hash).isNotBlank();
        assertThat(hash).isNotEqualTo("Password@123");
    }

    @Test
    @DisplayName("Should match password against hash")
    public void shouldMatchPasswordAgainstHash() {
        String hash = passwordServiceImplementation.hashPassword("password");
        boolean match =  passwordServiceImplementation.matchesPassword("password", hash);
        assertThat(match).isTrue();
    }

    @Test
    @DisplayName("Should return false when password does not match hash")
    public void shouldNotMatchPasswordAgainstHash() {
        String hash = passwordServiceImplementation.hashPassword("password");
        boolean match =  passwordServiceImplementation.matchesPassword("wrongPassword", hash);
        assertThat(match).isFalse();
    }
}
