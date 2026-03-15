package com.example.studycore.infrastructure.api.controllers.activity.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        String title,
        String type,
        String convertedHtml,
        UUID folderId,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

