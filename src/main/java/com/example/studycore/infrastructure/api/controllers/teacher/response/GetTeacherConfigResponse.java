package com.example.studycore.infrastructure.api.controllers.teacher.response;

import java.time.LocalTime;

public record GetTeacherConfigResponse(
        String pixKey,
        String pixKeyType,
        WorkHourResponse workHour
) {
    public record WorkHourResponse(
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ) {
    }
}
