package com.example.studycore.application.usecase.levelsubfolder.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LevelSubfolderOutput(
        UUID id,
        UUID levelFolderId,
        String name,
        Integer position,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

