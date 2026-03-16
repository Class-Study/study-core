package com.example.studycore.infrastructure.api.controllers.student.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record UpdateStudentRequest(
        String name,
        String phone,
        String avatarUrl,
        UUID levelProfileId,
        LocalTime classTime,
        List<String> classDays,
        Integer classDuration,
        BigDecimal classRate,
        String meetPlatform,
        String meetLink,
        LocalDate startDate // novo campo
) {
}

