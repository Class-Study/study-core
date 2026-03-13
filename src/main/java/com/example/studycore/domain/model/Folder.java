package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class Folder {

    private final UUID id;
    private final UUID studentId;
    private String name;
    private Integer position;
    private final OffsetDateTime createdAt;

    private Folder(UUID id, UUID studentId, String name, Integer position, OffsetDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = normalize(name);
        this.position = position;
        this.createdAt = createdAt;
        validate();
    }

    public static Folder create(UUID studentId, String name, Integer position) {
        return new Folder(
                UUID.randomUUID(),
                studentId,
                name,
                position,
                OffsetDateTime.now()
        );
    }

    public static Folder with(UUID id, UUID studentId, String name, Integer position, OffsetDateTime createdAt) {
        return new Folder(id, studentId, name, position, createdAt);
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Folder id cannot be null.");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Folder studentId cannot be null.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Folder name cannot be blank.");
        }
        if (position == null || position < 0) {
            throw new IllegalArgumentException("Folder position must be zero or greater.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Folder createdAt cannot be null.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
