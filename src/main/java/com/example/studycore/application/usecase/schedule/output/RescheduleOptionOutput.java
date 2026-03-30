package com.example.studycore.application.usecase.schedule.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record RescheduleOptionOutput(
        UUID scheduleId,
        String studentName,
        LocalDate currentDate,
        int durationMin,
        List<Option> options,
        boolean limitedOptions

) {
    public record Option(LocalDate date, LocalTime time) {
    }
}
