package com.example.studycore.application.usecase.student.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EvaluatedDaysOutput(
        int totalClassesPerDay,
        List<DayAvailability> availability
) {
    public record DayAvailability(
            String day,
            int totalClasses,
            int conflictCount,
            String status,
            List<Conflict> conflicts
    ) {
    }

    public record Conflict(
            String scheduleId,
            String studentId,
            LocalDate date,
            LocalTime startTime,
            int durationMin
    ) {
    }
}

