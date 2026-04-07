package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class LevelSubfolder {

    private final UUID id;
    private final UUID levelFolderId;
    private final String name;
    private final Integer position;
    private final UUID createdBy;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private LevelSubfolder(
            UUID id,
            UUID levelFolderId,
            String name,
            Integer position,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.levelFolderId = levelFolderId;
        this.name = normalize(name);
        this.position = position;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    public static LevelSubfolder create(
            UUID levelFolderId,
            String name,
            Integer position,
            UUID createdBy
    ) {
        final var now = OffsetDateTime.now();
        return new LevelSubfolder(
                UUID.randomUUID(),
                levelFolderId,
                name,
                position,
                createdBy,
                now,
                now
        );
    }

    public static LevelSubfolder with(
            UUID id,
            UUID levelFolderId,
            String name,
            Integer position,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new LevelSubfolder(id, levelFolderId, name, position, createdBy, createdAt, updatedAt);
    }

    public LevelSubfolder update(String name, Integer position) {
        final var newName = name != null ? name : this.name;
        final var newPosition = position != null ? position : this.position;
        return new LevelSubfolder(
                this.id,
                this.levelFolderId,
                newName,
                newPosition,
                this.createdBy,
                this.createdAt,
                OffsetDateTime.now()
        );
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("LevelSubfolder id cannot be null.");
        if (levelFolderId == null) throw new IllegalArgumentException("LevelSubfolder levelFolderId cannot be null.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("LevelSubfolder name cannot be blank.");
        if (position == null || position < 0) throw new IllegalArgumentException("LevelSubfolder position must be zero or greater.");
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}

