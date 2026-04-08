package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.UUID;

/**
 * Represents a material item in the PUT subfolder payload.
 * When {@code id} is non-null the item already exists and must be kept as-is.
 * When {@code id} is null a new {@code StudyMaterial} must be created.
 */
public record MaterialItemInput(
        UUID id,
        String title,
        String type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description
) {}

