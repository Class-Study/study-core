package com.example.studycore.infrastructure.api.controllers.student.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateStudentRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone,
        String avatarUrl,
        @NotNull UUID levelProfileId,
        @NotNull LocalTime classTime,
        @NotNull List<String> classDays,
        @NotNull Integer classDuration,
        @NotNull BigDecimal classRate,
        String meetPlatform,
        String meetLink,
        @NotNull LocalDate startDate
) {
}
