package com.example.studycore.application.usecase.studentnote.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetMyNotesOutput(
        UUID id,
        String type,
        String content,
        OffsetDateTime createdAt
) {
}

