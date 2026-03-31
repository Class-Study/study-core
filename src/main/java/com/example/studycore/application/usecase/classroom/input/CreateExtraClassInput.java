package com.example.studycore.application.usecase.classroom.input;

import com.example.studycore.domain.model.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateExtraClassInput(
        UUID studentId,
        UUID teacherId,
        ScheduleType type,
        LocalDate date,
        LocalTime startTime,
        Integer durationMin,
        String title
) {
}

