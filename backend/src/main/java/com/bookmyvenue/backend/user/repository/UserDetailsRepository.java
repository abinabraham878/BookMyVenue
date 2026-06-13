package com.bookmyvenue.backend.user.repository;

import com.bookmyvenue.backend.user.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, UUID> {

    boolean existsByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);
}
