package com.example.studycore.infrastructure.api.controllers.schedule.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateExtraClassRequest(
        @NotNull UUID studentId,
        @NotNull UUID teacherId,
        @NotNull String type,
        @NotNull LocalDate date,
        @NotNull LocalTime startTime,
        @NotNull Integer durationMin,
        @NotBlank String title
) {
}

