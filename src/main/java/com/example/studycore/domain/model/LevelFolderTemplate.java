package com.example.studycore.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class LevelFolderTemplate {

    private final UUID id;
    private final UUID levelFolderId;
    private final String title;
    private final String type;
    private final String originalFilename;
    private final String convertedHtml;
    private final UUID createdBy;
    private final OffsetDateTime createdAt;

    private LevelFolderTemplate(
            UUID id,
            UUID levelFolderId,
            String title,
            String type,
            String originalFilename,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.levelFolderId = levelFolderId;
        this.title = normalize(title);
        this.type = normalizeType(type);
        this.originalFilename = originalFilename;
        this.convertedHtml = normalize(convertedHtml);
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        validate();
    }

    public static LevelFolderTemplate create(
            UUID levelFolderId,
            String title,
            String type,
            String originalFilename,
            String convertedHtml,
            UUID createdBy
    ) {
        return new LevelFolderTemplate(
                UUID.randomUUID(),
                levelFolderId,
                title,
                type,
                originalFilename,
                convertedHtml,
                createdBy,
                OffsetDateTime.now()
        );
    }

    public static LevelFolderTemplate with(
            UUID id,
            UUID levelFolderId,
            String title,
            String type,
            String originalFilename,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt
    ) {
        return new LevelFolderTemplate(
                id,
                levelFolderId,
                title,
                type,
                originalFilename,
                convertedHtml,
                createdBy,
                createdAt
        );
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Template id cannot be null.");
        }
        if (levelFolderId == null) {
            throw new IllegalArgumentException("Level folder id cannot be null.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Template title cannot be blank.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Template type cannot be blank.");
        }
        if (convertedHtml == null || convertedHtml.isBlank()) {
            throw new IllegalArgumentException("Template converted HTML cannot be blank.");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by user id cannot be null.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeType(String type) {
        if (type == null) {
            return "EXERCISE";
        }
        String normalized = type.trim().toUpperCase();
        if (!normalized.matches("^(EXERCISE|WORKSPACE)$")) {
            return "EXERCISE";
        }
        return normalized;
    }
}

