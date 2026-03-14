package com.example.studycore.application.usecase.studentnote.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudentNoteOutput(
        UUID id,
        String type,
        String content,
        OffsetDateTime createdAt
) {
}

