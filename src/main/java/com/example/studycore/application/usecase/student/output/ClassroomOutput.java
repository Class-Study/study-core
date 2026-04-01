package com.example.studycore.application.usecase.student.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ClassroomOutput(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        Integer durationMin
) {
}
