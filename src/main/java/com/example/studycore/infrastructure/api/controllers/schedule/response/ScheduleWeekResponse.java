package com.example.studycore.infrastructure.api.controllers.schedule.response;

import java.util.List;

public record ScheduleWeekResponse(
        String weekStart,
        String weekEnd,
        List<ScheduleEventResponse> events
)
{
}

