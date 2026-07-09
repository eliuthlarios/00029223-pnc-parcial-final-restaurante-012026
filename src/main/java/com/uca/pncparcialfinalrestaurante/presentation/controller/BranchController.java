package com.uca.pncparcialfinalrestaurante.presentation.controller;

import com.uca.pncparcialfinalrestaurante.business.service.BranchService;
import com.uca.pncparcialfinalrestaurante.presentation.dto.branch.BranchRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.branch.BranchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<BranchResponse> create(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> findAll() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.findById(id));
    }
}
