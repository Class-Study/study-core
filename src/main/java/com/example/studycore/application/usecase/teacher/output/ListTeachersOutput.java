package com.example.studycore.application.usecase.teacher.output;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListTeachersOutput(
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
