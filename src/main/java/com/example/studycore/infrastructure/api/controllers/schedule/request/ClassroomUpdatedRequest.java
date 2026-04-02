package com.example.studycore.infrastructure.api.controllers.schedule.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClassroomUpdatedRequest(
        LocalDate newDate,
        LocalTime newTime
) {
}
