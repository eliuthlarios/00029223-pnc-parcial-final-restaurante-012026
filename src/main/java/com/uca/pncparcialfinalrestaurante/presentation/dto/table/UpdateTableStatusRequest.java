package com.uca.pncparcialfinalrestaurante.presentation.dto.table;

import com.uca.pncparcialfinalrestaurante.data.enums.TableStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTableStatusRequest(@NotNull TableStatus status) {
}
