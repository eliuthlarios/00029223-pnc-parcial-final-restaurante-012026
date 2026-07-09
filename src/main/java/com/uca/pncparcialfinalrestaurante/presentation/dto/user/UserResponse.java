package com.uca.pncparcialfinalrestaurante.presentation.dto.user;

import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        RoleName role,
        Long branchId,
        String branchName,
        boolean active
) {
}
