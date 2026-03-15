package com.example.studycore.infrastructure.api.controllers.studentnote.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetMyNotesResponse(
        UUID id,
        String type,
        String content,
        OffsetDateTime createdAt
) {
}

