package com.bookmyvenue.backend.user.service.interfaces;

import com.bookmyvenue.backend.auth.dto.request.RegisterRequest;
import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.auth.service.implementations.EmailVerificationServiceImplementation;
import com.bookmyvenue.backend.user.enums.Role;
import com.bookmyvenue.backend.user.repository.UserDetailsRepository;
import com.bookmyvenue.backend.user.repository.UserRepository;
import com.bookmyvenue.backend.user.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void registerUser(RegisterRequest registerRequest, Role role) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
