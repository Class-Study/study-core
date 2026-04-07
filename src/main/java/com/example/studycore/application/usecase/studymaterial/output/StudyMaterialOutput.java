package com.example.studycore.application.usecase.studymaterial.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudyMaterialOutput(
        UUID id,
        UUID levelFolderId,
        UUID subfolderId,
        String subfolderType,
        String title,
        String type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

