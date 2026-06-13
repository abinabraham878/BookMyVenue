package com.bookmyvenue.backend.user.entity;

import com.bookmyvenue.backend.common.entity.BaseEntity;
import com.bookmyvenue.backend.user.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_roles")
@IdClass(UserRoleId.class)
public class UserRoleEntity extends BaseEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, length = 50)
    private Role roleName;
}
