package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetMyActivitiesResponse(
        List<FolderWithActivities> folders
) {
    public record FolderWithActivities(
            UUID id,
            String name,
            Integer position,
            List<ActivityItem> activities
    ) {
    }

    public record ActivityItem(
            UUID id,
            String title,
            String type,
            String convertedHtml,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }
}

