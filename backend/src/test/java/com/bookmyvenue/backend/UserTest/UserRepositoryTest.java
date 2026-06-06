package com.bookmyvenue.backend.UserTest;

import com.bookmyvenue.backend.user.entity.UserEntity;
import com.bookmyvenue.backend.user.enums.UserStatus;
import com.bookmyvenue.backend.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return true when Email exists")
    void shouldReturnTrueWhenEmailExists() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("user@gmail.com");
        user.setPasswordHash("password-hash");
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("user@gmail.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("unknown@gmail.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("john@gmail.com");
        user.setPasswordHash("hashed-password");
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        var result = userRepository.findByEmail("john@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail())
                .isEqualTo("john@gmail.com");
    }
}
