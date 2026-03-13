package com.example.studycore.infrastructure.api.controllers.folder.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListFoldersResponse(
        List<Item> folders
) {
    public record Item(
            UUID id,
            UUID studentId,
            String name,
            Integer position,
            OffsetDateTime createdAt
    ) {
    }
}
