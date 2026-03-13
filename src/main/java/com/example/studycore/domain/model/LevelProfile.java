package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class LevelProfile {

    private final UUID id;
    private String name;
    private String code;
    private String icon;
    private String description;
    private final boolean isSystem;
    private final UUID createdBy;
    private List<LevelFolder> folders;
    private final OffsetDateTime createdAt;

    private LevelProfile(
            UUID id,
            String name,
            String code,
            String icon,
            String description,
            boolean isSystem,
            UUID createdBy,
            List<LevelFolder> folders,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.name = normalize(name);
        this.code = normalize(code) == null ? null : normalize(code).toLowerCase();
        this.icon = normalize(icon);
        this.description = normalizeNullable(description);
        this.isSystem = isSystem;
        this.createdBy = createdBy;
        this.folders = folders;
        this.createdAt = createdAt;

        validate();
    }

    public static LevelProfile create(
            String name,
            String code,
            String icon,
            String description,
            boolean isSystem,
            UUID createdBy,
            List<LevelFolder> folders
    ) {
        return new LevelProfile(
                UUID.randomUUID(),
                name,
                code,
                icon,
                description,
                isSystem,
                createdBy,
                folders,
                OffsetDateTime.now()
        );
    }

    public static LevelProfile with(
            UUID id,
            String name,
            String code,
            String icon,
            String description,
            boolean isSystem,
            UUID createdBy,
            List<LevelFolder> folders,
            OffsetDateTime createdAt
    ) {
        return new LevelProfile(
                id,
                name,
                code,
                icon,
                description,
                isSystem,
                createdBy,
                folders,
                createdAt
        );
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Level profile id cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Level profile name cannot be null or blank");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Level profile code cannot be null or blank");
        }
        if (icon == null || icon.isBlank()) {
            throw new IllegalArgumentException("Level profile icon cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Level profile createdAt cannot be null");
        }
        if (!isSystem && createdBy == null) {
            throw new IllegalArgumentException("Level profile createdBy cannot be null for non-system profiles");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        final var normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
