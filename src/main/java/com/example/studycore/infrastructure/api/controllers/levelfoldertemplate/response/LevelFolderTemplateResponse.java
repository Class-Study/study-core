package com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LevelFolderTemplateResponse(
        UUID id,
        UUID levelFolderId,
        UUID subfolderId,
        String title,
        String type,
        String originalFilename,
        String convertedHtml,
        OffsetDateTime createdAt
) {}

