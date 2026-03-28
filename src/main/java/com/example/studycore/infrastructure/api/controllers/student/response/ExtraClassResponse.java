package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExtraClassResponse(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        Integer durationMin
) {
}
