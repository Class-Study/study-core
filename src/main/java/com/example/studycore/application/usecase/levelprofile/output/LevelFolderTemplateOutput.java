package com.example.studycore.application.usecase.levelprofile.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LevelFolderTemplateOutput(
        UUID id,
        UUID levelFolderId,
        String title,
        String type,
        String originalFilename,
        OffsetDateTime createdAt
) {}

