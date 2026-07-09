package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import com.uca.pncparcialfinalrestaurante.data.entity.RefreshToken;
import com.uca.pncparcialfinalrestaurante.data.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.security.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public String createRefreshToken(AppUser user) {
        String rawToken = UUID.randomUUID() + "." + UUID.randomUUID();
        Instant now = Instant.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.refreshTokenExpirationDays() * 24 * 60 * 60))
                .lastUsedAt(now)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional
    public RefreshToken validateAndTouch(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

        if (!token.isUsable()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token expirado o revocado");
        }

        token.setLastUsedAt(Instant.now());
        return token;
    }

    @Transactional
    public void revokeAllByUser(AppUser user) {
        refreshTokenRepository.revokeAllByUserId(user.getId(), Instant.now());
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No se pudo calcular hash SHA-256", ex);
        }
    }
}
