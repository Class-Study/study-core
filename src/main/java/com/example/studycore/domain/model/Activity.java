package com.example.studycore.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class Activity {

    private static final Set<String> ALLOWED_TYPES = Set.of("EXERCISE", "WORKSPACE", "MATERIAL");

    private final UUID id;
    private final UUID folderId;
    private UUID subfolderId; // nullable — links to student_subfolders
    private String title;
    private String type;
    private String convertedHtml;
    private final UUID createdBy;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    @Setter
    private String snapshot;  // base64 gzip do HTML completo

    private Activity(
            UUID id,
            UUID folderId,
            UUID subfolderId,
            String title,
            String type,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.folderId = folderId;
        this.subfolderId = subfolderId;
        this.title = normalize(title);
        this.type = normalizeType(type);
        this.convertedHtml = convertedHtml == null ? "" : convertedHtml;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.snapshot = "";
        validate();
    }

    /** Legacy factory — no subfolder context */
    public static Activity create(UUID folderId, String title, String type, String convertedHtml, UUID createdBy) {
        final var now = OffsetDateTime.now();
        return new Activity(UUID.randomUUID(), folderId, null, title, type, convertedHtml, createdBy, now, now);
    }

    /** Factory with subfolder context for template/material propagation */
    public static Activity createWithSubfolder(UUID folderId, UUID subfolderId, String title, String type, String convertedHtml, UUID createdBy) {
        final var now = OffsetDateTime.now();
        return new Activity(UUID.randomUUID(), folderId, subfolderId, title, type, convertedHtml, createdBy, now, now);
    }

    public static Activity with(
            UUID id,
            UUID folderId,
            String title,
            String type,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Activity(id, folderId, null, title, type, convertedHtml, createdBy, createdAt, updatedAt);
    }

    public static Activity withYjsState(
            UUID id,
            UUID folderId,
            UUID subfolderId,
            String title,
            String type,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            String snapshot
    ) {
        Activity activity = new Activity(id, folderId, subfolderId, title, type, convertedHtml, createdBy, createdAt, updatedAt);
        activity.snapshot = snapshot;
        return activity;
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Activity id cannot be null.");
        }
        if (folderId == null) {
            throw new IllegalArgumentException("Activity folderId cannot be null.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Activity title cannot be blank.");
        }
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException("Activity type must be EXERCISE, MATERIAL or WORKSPACE.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Activity createdAt cannot be null.");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("Activity updatedAt cannot be null.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeType(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}
