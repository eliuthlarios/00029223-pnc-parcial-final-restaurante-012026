package com.uca.pncparcialfinalrestaurante.presentation.controller;

import com.uca.pncparcialfinalrestaurante.business.service.AuthService;
import com.uca.pncparcialfinalrestaurante.business.service.CurrentUserService;
import com.uca.pncparcialfinalrestaurante.business.service.UserService;
import com.uca.pncparcialfinalrestaurante.presentation.dto.auth.*;
import com.uca.pncparcialfinalrestaurante.presentation.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/register-client")
    public ResponseEntity<UserResponse> registerClient(@Valid @RequestBody RegisterClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerClient(request));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(currentUserService.getCurrentUser(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.toResponse(currentUserService.getCurrentUser()));
    }
}
