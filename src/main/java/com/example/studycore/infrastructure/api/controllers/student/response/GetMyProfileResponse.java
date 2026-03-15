package com.example.studycore.infrastructure.api.controllers.student.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetMyProfileResponse(
        UUID id,
        String name,
        String email,
        String avatarUrl,
        String status,
        LevelProfileMinimal levelProfile,
        List<String> classDays,
        LocalTime classTime,
        Integer classDuration,
        BigDecimal classRate,
        String meetPlatform,
        String meetLink,
        LocalDate startDate,
        OffsetDateTime createdAt,
        TeacherInfo teacher
) {
    public record LevelProfileMinimal(
            UUID id,
            String name,
            String code
    ) {
    }

    public record TeacherInfo(
            UUID id,
            String name,
            String email,
            String phone
    ) {
    }
}


