package com.example.studycore.application.usecase.activity.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetActivityOutput(
        UUID id,
        UUID folderId,
        String title,
        String type,
        String contentHtml,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

