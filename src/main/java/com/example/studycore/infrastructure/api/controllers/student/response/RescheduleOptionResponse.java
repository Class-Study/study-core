package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RescheduleOptionResponse(
        String scheduleId,
        String studentName,
        LocalDate currentDate,
        int durationMin,
        List<Option> options
) {
    public record Option(LocalDate date, LocalTime time) {}
}
