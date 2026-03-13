package com.example.studycore.infrastructure.api.controllers.levelprofile.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListLevelProfilesResponse(
        List<Item> levelProfiles
) {
    public record Item(
            UUID id,
            String name,
            String code,
            String icon,
            String description,
            boolean isSystem,
            UUID createdBy,
            List<LevelFolderResponse> folders,
            OffsetDateTime createdAt
    ) {
    }
}

