package com.uca.pncparcialfinalrestaurante.data.repository;

import com.uca.pncparcialfinalrestaurante.data.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update RefreshToken rt
            set rt.revoked = true, rt.revokedAt = :revokedAt
            where rt.user.id = :userId and rt.revoked = false
            """)
    void revokeAllByUserId(@Param("userId") Long userId, @Param("revokedAt") Instant revokedAt);
}
