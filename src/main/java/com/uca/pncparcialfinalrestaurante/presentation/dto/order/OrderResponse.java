package com.uca.pncparcialfinalrestaurante.presentation.dto.order;

import com.uca.pncparcialfinalrestaurante.data.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        String customerName,
        Long tableId,
        String tableCode,
        Long branchId,
        String branchName,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {
}
