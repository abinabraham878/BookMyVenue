package com.bookmyvenue.backend.auth.service.implementations;

import com.bookmyvenue.backend.auth.interfaces.PasswordService;
import com.bookmyvenue.backend.common.constants.RegexConstants;
import com.bookmyvenue.backend.common.exception.BusinessException;
import com.bookmyvenue.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImplementation implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void validatePassword(String password, String confirmPassword) {
        if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_MISMATCH);
        }
        if (!password.matches(RegexConstants.PASSWORD)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }
    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
