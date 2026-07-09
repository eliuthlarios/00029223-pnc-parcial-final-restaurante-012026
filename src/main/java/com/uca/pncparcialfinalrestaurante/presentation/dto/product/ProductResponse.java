package com.uca.pncparcialfinalrestaurante.presentation.dto.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean active
) {
}
