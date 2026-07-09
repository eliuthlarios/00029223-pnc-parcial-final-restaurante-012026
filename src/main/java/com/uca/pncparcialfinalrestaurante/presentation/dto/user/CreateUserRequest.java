package com.uca.pncparcialfinalrestaurante.presentation.dto.user;

import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(min = 6, max = 80) String password,
        @NotBlank @Size(max = 120) String fullName,
        @NotNull RoleName role,
        Long branchId
) {
}
