package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import com.uca.pncparcialfinalrestaurante.data.entity.RefreshToken;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.data.repository.AppUserRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.auth.*;
import com.uca.pncparcialfinalrestaurante.presentation.dto.user.UserResponse;
import com.uca.pncparcialfinalrestaurante.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        return buildTokenResponse(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateAndTouch(request.refreshToken());
        return new TokenResponse(
                jwtService.generateAccessToken(refreshToken.getUser()),
                request.refreshToken(),
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    @Transactional
    public UserResponse registerClient(RegisterClientRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw BusinessException.badRequest("El username ya está registrado");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .fullName(request.fullName())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(RoleName.CLIENTE)
                .active(true)
                .tokenVersion(0)
                .passwordChangedAt(Instant.now())
                .build();

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(AppUser user, ChangePasswordRequest request) {
        AppUser managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.currentPassword(), managedUser.getPasswordHash())) {
            throw BusinessException.badRequest("La contraseña actual no es correcta");
        }

        managedUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        managedUser.setPasswordChangedAt(Instant.now());
        managedUser.setTokenVersion(managedUser.getTokenVersion() + 1);
        refreshTokenService.revokeAllByUser(managedUser);
    }

    private TokenResponse buildTokenResponse(AppUser user) {
        return new TokenResponse(
                jwtService.generateAccessToken(user),
                refreshTokenService.createRefreshToken(user),
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getBranch() == null ? null : user.getBranch().getId(),
                user.getBranch() == null ? null : user.getBranch().getName(),
                user.isActive()
        );
    }
}
