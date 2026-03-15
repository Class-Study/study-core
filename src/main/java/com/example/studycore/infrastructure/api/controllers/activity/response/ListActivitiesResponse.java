package com.example.studycore.infrastructure.api.controllers.activity.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListActivitiesResponse(
        List<Item> activities
) {
    public record Item(
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
}
