package com.bookmyvenue.backend.UserTest;

import com.bookmyvenue.backend.auth.dto.request.RegisterRequest;
import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.auth.service.implementations.EmailVerificationServiceImplementation;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import com.bookmyvenue.backend.user.entity.UserEntity;
import com.bookmyvenue.backend.user.entity.UserRoleEntity;
import com.bookmyvenue.backend.user.enums.Role;
import com.bookmyvenue.backend.user.enums.UserStatus;
import com.bookmyvenue.backend.user.repository.UserDetailsRepository;
import com.bookmyvenue.backend.user.repository.UserRepository;
import com.bookmyvenue.backend.user.repository.UserRoleRepository;
import com.bookmyvenue.backend.user.service.interfaces.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordService passwordService;
    @Mock
    private EmailVerificationServiceImplementation emailVerificationServiceImplementation;
    @InjectMocks
    private RegistrationService registrationService;

    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        registerRequest = new RegisterRequest(
                "john@gmail.com",
                "Password@123",
                "Password@123",
                "John",
                "Doe",
                "+91",
                "9876543210"
        );
    }

    @Test
    @DisplayName("Should register user successfully")
    public void registerUserSuccessfully() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        SuccessResponse response = registrationService.registerUser(registerRequest, Role.USER);

        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(ErrorCode.AUTH_REGISTRATION_SUCCESS.name());

        verify(userRepository).save(any());
        verify(userDetailsRepository).save(any());
        verify(userRoleRepository).save(any());
        verify(emailVerificationServiceImplementation).createAndSendVerificationEmail(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    public void registerUserFailure() {
        UserEntity user =  new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("john@gmail.com");
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        BusinessException exception = catchThrowableOfType(
                () -> registrationService.registerUser(registerRequest, Role.USER),
                BusinessException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should resend verification email when user is pending verification")
    void shouldResendVerificationEmail() {

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("john@gmail.com");
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        SuccessResponse response = registrationService.registerUser(registerRequest, Role.USER);
        assertThat(response.statusCode()).isEqualTo(ErrorCode.AUTH_EMAIL_VERIFICATION_RESENT.name());
        verify(emailVerificationServiceImplementation).resendVerificationEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when phone number already exists")
    void shouldThrowExceptionWhenPhoneNumberAlreadyExists() {

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(true);

        BusinessException exception = catchThrowableOfType(
                        () -> registrationService.registerUser(registerRequest, Role.USER),
                        BusinessException.class
                );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_PHONE_NUMBER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should normalize email before saving")
    void shouldNormalizeEmailBeforeSaving() {
        RegisterRequest request = new RegisterRequest(
                "John@GMAIL.COM",
                "Password@123",
                "Password@123",
                "John",
                "Doe",
                "+91",
                "9876543210"
        );
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(request, Role.USER);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("john@gmail.com");
    }

    @Test
    @DisplayName("Should create user with pending verification status")
    void shouldCreateUserWithPendingVerificationStatus() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(registerRequest, Role.USER);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
    }

    @Test
    @DisplayName("Should assign USER role")
    void shouldAssignUserRole() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(registerRequest, Role.USER);
        ArgumentCaptor<UserRoleEntity> captor = ArgumentCaptor.forClass(UserRoleEntity.class);
        verify(userRoleRepository).save(captor.capture());
        assertThat(captor.getValue().getRoleName()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Should assign venue owner role")
    void shouldAssignVenueOwnerRole() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(registerRequest, Role.VENUE_OWNER);
        ArgumentCaptor<UserRoleEntity> captor = ArgumentCaptor.forClass(UserRoleEntity.class);
        verify(userRoleRepository).save(captor.capture());
        assertThat(captor.getValue().getRoleName()).isEqualTo(Role.VENUE_OWNER);
    }

    @Test
    @DisplayName("Should validate password before registration")
    void shouldValidatePasswordBeforeRegistration() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(registerRequest, Role.USER);
        verify(passwordService).validatePassword(registerRequest.password(), registerRequest.confirmPassword());
    }

    @Test
    @DisplayName("Should send verification email after registration")
    void shouldSendVerificationEmailAfterRegistration() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userDetailsRepository.existsByCountryCodeAndPhoneNumber(any(), any())).thenReturn(false);
        when(passwordService.hashPassword(any())).thenReturn("hashed-password");
        registrationService.registerUser(registerRequest, Role.USER);
        verify(emailVerificationServiceImplementation).createAndSendVerificationEmail(any(UserEntity.class));
    }
}
