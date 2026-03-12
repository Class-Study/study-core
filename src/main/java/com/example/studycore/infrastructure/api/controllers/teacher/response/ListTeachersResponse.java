package com.example.studycore.infrastructure.api.controllers.teacher.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListTeachersResponse(
        List<TeacherSummary> teachers
) {
    public record TeacherSummary(
            UUID id,
            String name,
            String email,
            String status,
            String phone,
            OffsetDateTime createdAt
    ) {
    }
}
