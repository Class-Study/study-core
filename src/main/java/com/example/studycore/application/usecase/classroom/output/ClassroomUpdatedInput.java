package com.example.studycore.application.usecase.classroom.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ClassroomUpdatedInput(
        UUID id,
        LocalDate newDate,
        LocalTime newTime
) {
}
