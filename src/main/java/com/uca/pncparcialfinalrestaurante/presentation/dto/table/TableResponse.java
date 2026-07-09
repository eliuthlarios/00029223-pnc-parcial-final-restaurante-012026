package com.uca.pncparcialfinalrestaurante.presentation.dto.table;

import com.uca.pncparcialfinalrestaurante.data.enums.TableStatus;

public record TableResponse(
        Long id,
        String code,
        Integer capacity,
        TableStatus status,
        Long branchId,
        String branchName
) {
}
