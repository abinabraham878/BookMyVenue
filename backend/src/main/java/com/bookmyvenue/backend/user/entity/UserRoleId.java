package com.bookmyvenue.backend.user.entity;

import com.bookmyvenue.backend.user.enums.Role;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    private UUID userId;
    private Role roleName;
}
