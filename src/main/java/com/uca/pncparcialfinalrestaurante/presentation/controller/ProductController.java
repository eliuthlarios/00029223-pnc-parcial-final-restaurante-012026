package com.uca.pncparcialfinalrestaurante.presentation.controller;

import com.uca.pncparcialfinalrestaurante.business.service.ProductService;
import com.uca.pncparcialfinalrestaurante.presentation.dto.product.ProductRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.product.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findActive() {
        return ResponseEntity.ok(productService.findActive());
    }
}
