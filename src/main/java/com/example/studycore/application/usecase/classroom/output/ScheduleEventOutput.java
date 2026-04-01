package com.example.studycore.application.usecase.classroom.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleEventOutput(
        UUID id,
        UUID studentId,
        String studentName,
        LocalDate date,
        LocalTime startTime,
        Integer durationMin,
        String type,
        String title,
        String meetLink,
        String meetPlatform,
        String studentStatus,
        String levelCode
) {
}

