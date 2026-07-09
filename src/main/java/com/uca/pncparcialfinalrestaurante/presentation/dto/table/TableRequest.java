package com.uca.pncparcialfinalrestaurante.presentation.dto.table;

import com.uca.pncparcialfinalrestaurante.data.enums.TableStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableRequest(
        @NotBlank @Size(max = 30) String code,
        @NotNull @Min(1) Integer capacity,
        @NotNull TableStatus status,
        @NotNull Long branchId
) {
}
