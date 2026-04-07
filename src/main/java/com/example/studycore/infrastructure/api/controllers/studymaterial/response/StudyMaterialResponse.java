package com.example.studycore.infrastructure.api.controllers.studymaterial.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudyMaterialResponse(
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

