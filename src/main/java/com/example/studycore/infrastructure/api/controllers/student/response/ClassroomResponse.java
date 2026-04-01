package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ClassroomResponse(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        Integer durationMin
) {
}
