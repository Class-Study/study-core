package com.example.studycore.application.usecase.activity.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudentActivityOutput(
        UUID id,
        String title,
        String type,
        String folderName,
        UUID folderId,
        OffsetDateTime createdAt
) {
}

