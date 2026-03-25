package com.example.studycore.application.usecase.teacher.output;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetTeacherOutput(
        UUID id,
        String name,
        String email,
        String role,
        String status,
        String avatarUrl,
        String phone,
        String provisoryPass,
        OffsetDateTime lastSeenAt,
        OffsetDateTime createdAt
) {
}
