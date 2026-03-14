package com.example.studycore.infrastructure.api.controllers.activity.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudentActivityResponse(
        UUID id,
        String title,
        String type,
        String folderName,
        UUID folderId,
        OffsetDateTime createdAt
) {
}

