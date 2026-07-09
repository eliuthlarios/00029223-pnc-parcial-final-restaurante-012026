package com.uca.pncparcialfinalrestaurante.presentation.controller;

import com.uca.pncparcialfinalrestaurante.business.service.CurrentUserService;
import com.uca.pncparcialfinalrestaurante.business.service.OrderService;
import com.uca.pncparcialfinalrestaurante.presentation.dto.order.OrderCreateRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.order.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(currentUserService.getCurrentUser(), request));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<OrderResponse>> findMine() {
        return ResponseEntity.ok(orderService.findMine(currentUserService.getCurrentUser()));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<List<OrderResponse>> findByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(orderService.findByBranch(currentUserService.getCurrentUser(), branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(currentUserService.getCurrentUser(), id));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<OrderResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirm(currentUserService.getCurrentUser(), id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(currentUserService.getCurrentUser(), id));
    }
}
