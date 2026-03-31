package com.example.studycore.application.usecase.classroom.input;

import java.time.LocalDate;
import java.util.UUID;

public record ScheduleWeekInput(
        LocalDate weekStart,
        LocalDate weekEnd,
        UUID teacherId
) {
}
