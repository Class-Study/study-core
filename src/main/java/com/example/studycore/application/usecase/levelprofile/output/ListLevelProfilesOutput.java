package com.example.studycore.application.usecase.levelprofile.output;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListLevelProfilesOutput(
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
            List<LevelFolderOutput> folders,
            OffsetDateTime createdAt
    ) {
    }
}

