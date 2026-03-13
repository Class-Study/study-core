package com.example.studycore.application.usecase.folder.output;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListFoldersOutput(
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
