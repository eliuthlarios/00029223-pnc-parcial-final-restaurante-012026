package com.uca.pncparcialfinalrestaurante.presentation.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull Long tableId,
        Long customerId,
        @NotEmpty List<@Valid OrderItemRequest> items
) {
}
