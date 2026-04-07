package com.example.studycore.infrastructure.api.controllers.levelsubfolder.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LevelSubfolderResponse(
        UUID id,
        UUID levelFolderId,
        String name,
        Integer position,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

