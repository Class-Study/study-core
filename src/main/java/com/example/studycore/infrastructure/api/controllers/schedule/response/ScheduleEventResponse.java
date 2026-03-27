package com.example.studycore.infrastructure.api.controllers.schedule.response;

import java.util.UUID;

public record ScheduleEventResponse(
        String id,
        UUID studentId,
        String studentName,
        String date,
        String startTime,
        Integer durationMin,
        String type,
        String title,
        String meetLink,
        String meetPlatform,
        String studentStatus,
        String levelCode
)
{
}

