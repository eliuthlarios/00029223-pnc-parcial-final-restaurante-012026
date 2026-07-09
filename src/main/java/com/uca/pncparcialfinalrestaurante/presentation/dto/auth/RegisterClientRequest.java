package com.uca.pncparcialfinalrestaurante.presentation.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterClientRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(min = 6, max = 80) String password,
        @NotBlank @Size(max = 120) String fullName
) {
}
