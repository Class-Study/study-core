package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.UUID;

/**
 * Represents an exercise item in the PUT subfolder payload.
 * When {@code id} is non-null the item already exists and must be kept as-is.
 * When {@code id} is null a new {@code LevelFolderTemplate} must be created.
 */
public record ExerciseItemInput(
        UUID id,
        String title,
        String type,
        String originalFilename,
        String convertedHtml
) {}

