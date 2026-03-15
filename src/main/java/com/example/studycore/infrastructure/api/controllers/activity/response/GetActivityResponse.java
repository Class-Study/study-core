package com.example.studycore.infrastructure.api.controllers.activity.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetActivityResponse(
        UUID id,
        UUID folderId,
        String title,
        String type,
        String convertedHtml,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
