package com.example.studycore.application.usecase.student.input;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record UpdateStudentInput(
        UUID id,
        UUID teacherId,
        String name,
        String phone,
        String avatarUrl,
        UUID levelProfileId,
        LocalTime classTime,
        List<String> classDays,
        Integer classDuration,
        BigDecimal classRate,
        String meetPlatform,
        String meetLink
) {
}

