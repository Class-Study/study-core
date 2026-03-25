package com.example.studycore.infrastructure.api.controllers.teacher.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetTeacherResponse(
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
