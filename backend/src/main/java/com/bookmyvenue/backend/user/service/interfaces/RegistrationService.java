package com.bookmyvenue.backend.user.service.interfaces;

import com.bookmyvenue.backend.auth.dto.request.RegisterRequest;
import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.auth.service.implementations.EmailVerificationServiceImplementation;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import com.bookmyvenue.backend.user.entity.UserDetailsEntity;
import com.bookmyvenue.backend.user.entity.UserEntity;
import com.bookmyvenue.backend.user.entity.UserRoleEntity;
import com.bookmyvenue.backend.user.enums.Role;
import com.bookmyvenue.backend.user.enums.UserStatus;
import com.bookmyvenue.backend.user.repository.UserDetailsRepository;
import com.bookmyvenue.backend.user.repository.UserRepository;
import com.bookmyvenue.backend.user.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService implements RegistrationServiceInterface {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordService passwordService;
    private final EmailVerificationServiceImplementation emailVerificationServiceImplementation;

    @Override
    @Transactional
    public SuccessResponse registerUser(RegisterRequest registerRequest, Role role) {
        // Checking the user email already exists
        String email = normalizeEmail(registerRequest.email());
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            if (user.getStatus() == UserStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
            }
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                emailVerificationServiceImplementation.resendVerificationEmail(user.getEmail());
                return new SuccessResponse(
                        ErrorCode.AUTH_EMAIL_VERIFICATION_RESENT.name(),
                        ErrorCode.AUTH_EMAIL_VERIFICATION_RESENT.getMessage(),
                        ErrorCode.AUTH_EMAIL_VERIFICATION_RESENT.getHttpStatus().value()
                );
            }
        }
        // Checking user phone number already exists
        boolean phoneNumberExists = userDetailsRepository.existsByCountryCodeAndPhoneNumber(
                registerRequest.countryCode(), registerRequest.phoneNumber()
        );
        if (phoneNumberExists) {
            throw new BusinessException(ErrorCode.AUTH_PHONE_NUMBER_ALREADY_EXISTS);
        }
        // Checking user password matches security requirements
        passwordService.validatePassword(registerRequest.password(), registerRequest.confirmPassword());
        String hashedPassword = passwordService.hashPassword(registerRequest.password());
        // Creating User Entity
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(email);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(userEntity);
        // Creating User Details
        UserDetailsEntity userDetails = new UserDetailsEntity();
        userDetails.setUserId(userId);
        userDetails.setFirstName(registerRequest.firstName().trim());
        userDetails.setLastName(registerRequest.lastName().trim());
        userDetails.setCountryCode(registerRequest.countryCode().trim());
        userDetails.setPhoneNumber(registerRequest.phoneNumber().trim());
        userDetailsRepository.save(userDetails);
        // Creating User Role
        UserRoleEntity roleEntity = new UserRoleEntity();
        roleEntity.setUserId(userId);
        roleEntity.setRoleName(role);
        userRoleRepository.save(roleEntity);
        emailVerificationServiceImplementation.createAndSendVerificationEmail(userEntity);
        return new SuccessResponse(
          ErrorCode.AUTH_REGISTRATION_SUCCESS.name(),
          ErrorCode.AUTH_REGISTRATION_SUCCESS.getMessage(),
          ErrorCode.AUTH_REGISTRATION_SUCCESS.getHttpStatus().value()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
