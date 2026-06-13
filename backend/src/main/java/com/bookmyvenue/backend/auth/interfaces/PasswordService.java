package com.bookmyvenue.backend.auth.interfaces;

public interface PasswordService {

    void validatePassword(String password, String confirmPassword);
    String hashPassword(String password);
    boolean matchesPassword(String rawPassword, String hashedPassword);

}
