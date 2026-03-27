package com.example.studycore.application.usecase.schedule.input;

import java.time.LocalDate;
import java.util.UUID;

public record ScheduleWeekInput(
        LocalDate weekStart,
        LocalDate weekEnd,
        UUID teacherId
) {
}
