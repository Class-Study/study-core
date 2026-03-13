package com.example.studycore.application.usecase.student.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateStudentInput(
        UUID teacherId,
        String name,
        String email,
        String phone,
        String avatarUrl,
        UUID levelProfileId,
        LocalTime classTime,
        List<String> classDays,
        Integer classDuration,
        BigDecimal classRate,
        String meetPlatform,
        String meetLink,
        LocalDate startDate
) {
}

