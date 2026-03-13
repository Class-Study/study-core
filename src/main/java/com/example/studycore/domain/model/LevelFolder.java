package com.example.studycore.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LevelFolder {

    private final UUID id;
    private final String name;
    private final Integer position;
    private final Integer initialFiles;

    private LevelFolder(UUID id, String name, Integer position, Integer initialFiles) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.initialFiles = initialFiles;
        validate();
    }

    public static LevelFolder create(String name, Integer position, Integer initialFiles) {
        return new LevelFolder(
                UUID.randomUUID(),
                normalize(name),
                position,
                initialFiles
        );
    }

    public static LevelFolder with(UUID id, String name, Integer position, Integer initialFiles) {
        return new LevelFolder(
                id,
                normalize(name),
                position,
                initialFiles
        );
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Level folder id cannot be null.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Level folder name cannot be blank.");
        }
        if (position == null || position < 0) {
            throw new IllegalArgumentException("Level folder position must be zero or greater.");
        }
        if (initialFiles == null || initialFiles < 0) {
            throw new IllegalArgumentException("Level folder initialFiles must be zero or greater.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
