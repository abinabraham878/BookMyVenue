package com.bookmyvenue.backend.user.entity;

import com.bookmyvenue.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "user_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_details_country_code_phone_number",
                        columnNames = {"country_code", "phone_number"}
                )
        }
)
public class UserDetailsEntity extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
}
