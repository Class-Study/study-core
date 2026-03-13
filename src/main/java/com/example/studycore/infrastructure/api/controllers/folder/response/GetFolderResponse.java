package com.example.studycore.infrastructure.api.controllers.folder.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetFolderResponse(
        UUID id,
        UUID studentId,
        String name,
        Integer position,
        OffsetDateTime createdAt
) {
}
