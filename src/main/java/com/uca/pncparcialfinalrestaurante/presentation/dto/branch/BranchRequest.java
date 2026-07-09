package com.uca.pncparcialfinalrestaurante.presentation.dto.branch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BranchRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 180) String address
) {
}
