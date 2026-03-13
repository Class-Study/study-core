package com.example.studycore.application.usecase.student.output;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListStudentsOutput(
        List<StudentItem> students
) {
    public record StudentItem(
            UUID id,
            String name,
            String email,
            String phone,
            String avatarUrl,
            String status,
            UUID teacherId,
            UUID levelProfileId,
            List<String> classDays,
            LocalTime classTime,
            Integer classDuration,
            BigDecimal classRate,
            String meetPlatform,
            String meetLink,
            LocalDate startDate,
            OffsetDateTime createdAt
    ) {
    }
}
