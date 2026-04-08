package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class StudentSubfolder {

    private final UUID id;
    private final UUID folderId;
    private final UUID levelSubfolderId; // nullable — reference to the level subfolder it was copied from
    private final String name;
    private final Integer position;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private StudentSubfolder(
            UUID id,
            UUID folderId,
            UUID levelSubfolderId,
            String name,
            Integer position,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.folderId = folderId;
        this.levelSubfolderId = levelSubfolderId;
        this.name = normalize(name);
        this.position = position;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    public static StudentSubfolder create(UUID folderId, UUID levelSubfolderId, String name, Integer position) {
        final var now = OffsetDateTime.now();
        return new StudentSubfolder(UUID.randomUUID(), folderId, levelSubfolderId, name, position, now, now);
    }

    public static StudentSubfolder with(
            UUID id,
            UUID folderId,
            UUID levelSubfolderId,
            String name,
            Integer position,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new StudentSubfolder(id, folderId, levelSubfolderId, name, position, createdAt, updatedAt);
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("StudentSubfolder id cannot be null.");
        if (folderId == null) throw new IllegalArgumentException("StudentSubfolder folderId cannot be null.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("StudentSubfolder name cannot be blank.");
        if (position == null || position < 0) throw new IllegalArgumentException("StudentSubfolder position must be zero or greater.");
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}

