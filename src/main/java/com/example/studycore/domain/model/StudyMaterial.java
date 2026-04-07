package com.example.studycore.domain.model;

import com.example.studycore.domain.model.enums.StudyMaterialType;
import com.example.studycore.domain.model.enums.SubfolderType;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class StudyMaterial {

    private final UUID id;
    private final UUID levelFolderId;
    private final SubfolderType subfolderType; // nullable — legacy field
    private final UUID subfolderId;            // canonical reference (new)
    private final String title;
    private final StudyMaterialType type;
    private final String url;
    private final String convertedHtml;
    private final String originalFilename;
    private final String description;
    private final UUID createdBy;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private StudyMaterial(
            UUID id,
            UUID levelFolderId,
            SubfolderType subfolderType,
            UUID subfolderId,
            String title,
            StudyMaterialType type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.levelFolderId = levelFolderId;
        this.subfolderType = subfolderType;
        this.subfolderId = subfolderId;
        this.title = normalize(title);
        this.type = type;
        this.url = url;
        this.convertedHtml = convertedHtml;
        this.originalFilename = originalFilename;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    /** New factory: uses real subfolderId */
    public static StudyMaterial create(
            UUID levelFolderId,
            UUID subfolderId,
            String title,
            StudyMaterialType type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy
    ) {
        final var now = OffsetDateTime.now();
        return new StudyMaterial(
                UUID.randomUUID(),
                levelFolderId,
                null,
                subfolderId,
                title,
                type,
                url,
                convertedHtml,
                originalFilename,
                description,
                createdBy,
                now,
                now
        );
    }

    /** Legacy factory: uses subfolderType string */
    public static StudyMaterial create(
            UUID levelFolderId,
            SubfolderType subfolderType,
            String title,
            StudyMaterialType type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy
    ) {
        final var now = OffsetDateTime.now();
        return new StudyMaterial(
                UUID.randomUUID(),
                levelFolderId,
                subfolderType,
                null,
                title,
                type,
                url,
                convertedHtml,
                originalFilename,
                description,
                createdBy,
                now,
                now
        );
    }

    public static StudyMaterial with(
            UUID id,
            UUID levelFolderId,
            SubfolderType subfolderType,
            UUID subfolderId,
            String title,
            StudyMaterialType type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new StudyMaterial(
                id, levelFolderId, subfolderType, subfolderId, title, type,
                url, convertedHtml, originalFilename, description,
                createdBy, createdAt, updatedAt
        );
    }

    /** Backward-compat with() without subfolderId */
    public static StudyMaterial with(
            UUID id,
            UUID levelFolderId,
            SubfolderType subfolderType,
            String title,
            StudyMaterialType type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return with(id, levelFolderId, subfolderType, null, title, type,
                url, convertedHtml, originalFilename, description,
                createdBy, createdAt, updatedAt);
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("StudyMaterial id cannot be null.");
        }
        if (levelFolderId == null && subfolderId == null) {
            throw new IllegalArgumentException("StudyMaterial must have either levelFolderId or subfolderId.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("StudyMaterial title cannot be blank.");
        }
        if (type == null) {
            throw new IllegalArgumentException("StudyMaterial type cannot be null.");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("StudyMaterial createdBy cannot be null.");
        }
        if ((type == StudyMaterialType.VIDEO || type == StudyMaterialType.LINK) && (url == null || url.isBlank())) {
            throw new IllegalArgumentException("URL is required for VIDEO and LINK material types.");
        }
        if (type == StudyMaterialType.DOCUMENT && (convertedHtml == null || convertedHtml.isBlank())) {
            throw new IllegalArgumentException("convertedHtml is required for DOCUMENT material type.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
