package com.example.studycore.application.usecase.schedule.output;

import java.time.LocalDate;
import java.util.List;

public record ScheduleWeekOutput(
        LocalDate weekStart,
        LocalDate weekEnd,
        List<ScheduleEventOutput> events
) {
}

