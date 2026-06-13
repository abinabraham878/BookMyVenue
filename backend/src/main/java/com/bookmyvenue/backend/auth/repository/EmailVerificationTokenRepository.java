package com.bookmyvenue.backend.auth.repository;

import com.bookmyvenue.backend.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    List<EmailVerificationToken> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}