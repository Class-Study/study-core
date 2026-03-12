package com.example.studycore.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private UUID id;
    private UUID userId;
    private String tokenHash;
    private Instant expiresAt;
    private boolean revoked;
    private Instant createdAt;
    private String userAgent;

    private RefreshToken() {
    }

    public static RefreshToken with(UUID id, UUID userId, String tokenHash,
                                    Instant expiresAt, boolean revoked,
                                    Instant createdAt, String userAgent) {
        final var refreshToken = new RefreshToken();
        refreshToken.id = id;
        refreshToken.userId = userId;
        refreshToken.tokenHash = tokenHash;
        refreshToken.expiresAt = expiresAt;
        refreshToken.revoked = revoked;
        refreshToken.createdAt = createdAt;
        refreshToken.userAgent = userAgent;
        return refreshToken;
    }

    public static RefreshToken newToken(UUID userId, String tokenHash,
                                        Instant expiresAt, String userAgent) {
        return with(null, userId, tokenHash, expiresAt, false, null, userAgent);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }
}

