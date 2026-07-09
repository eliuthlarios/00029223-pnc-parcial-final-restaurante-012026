package com.uca.pncparcialfinalrestaurante.presentation.controller;

import com.uca.pncparcialfinalrestaurante.business.service.CurrentUserService;
import com.uca.pncparcialfinalrestaurante.business.service.TableService;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.TableRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.TableResponse;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.UpdateTableStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<TableResponse> create(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(currentUserService.getCurrentUser(), request));
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> findVisibleTables() {
        return ResponseEntity.ok(tableService.findVisibleTables(currentUserService.getCurrentUser()));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<TableResponse>> findByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(tableService.findByBranch(currentUserService.getCurrentUser(), branchId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<TableResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateTableStatusRequest request) {
        return ResponseEntity.ok(tableService.updateStatus(currentUserService.getCurrentUser(), id, request));
    }
}
