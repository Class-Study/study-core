package com.example.studycore.application.usecase.classroom.output;

import com.example.studycore.domain.model.enums.ScheduleType;

import java.time.LocalDate;
import java.util.UUID;

public record RescheduleOptionInput(
        UUID teacherId,
        UUID schedulerId,
        ScheduleType scheduleType,
        LocalDate date
) {
}
