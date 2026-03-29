package com.example.studycore.application.usecase.student.input;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record EvaluateAvailabilityInput(
        UUID teacherId,
        List<String> days,
        LocalTime classTime,
        int durationMin,
        LocalDate startDate,
        int contractMonths
) {}
