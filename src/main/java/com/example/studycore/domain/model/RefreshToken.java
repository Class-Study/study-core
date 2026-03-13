package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final OffsetDateTime expiresAt;
    private final boolean revoked;
    private final OffsetDateTime createdAt;
    private final String userAgent;

    private RefreshToken(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            boolean revoked,
            OffsetDateTime createdAt,
            String userAgent
    ) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
        this.userAgent = userAgent;
    }

    public static RefreshToken with(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            boolean revoked,
            OffsetDateTime createdAt,
            String userAgent
    ) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt, userAgent);
    }

    public static RefreshToken create(
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            String userAgent
    ) {
        final var id = UUID.randomUUID();
        final var revoked = false;
        final var createdAt = OffsetDateTime.now();
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt, userAgent);
    }

    public boolean isExpired(OffsetDateTime now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }
}