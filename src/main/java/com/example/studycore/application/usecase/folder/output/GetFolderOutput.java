package com.example.studycore.application.usecase.folder.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetFolderOutput(
        UUID id,
        UUID studentId,
        String name,
        Integer position,
        OffsetDateTime createdAt
) {
}
