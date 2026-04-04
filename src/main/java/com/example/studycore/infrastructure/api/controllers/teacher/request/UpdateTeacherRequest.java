package com.example.studycore.infrastructure.api.controllers.teacher.request;

import java.time.LocalTime;

public record UpdateTeacherRequest(
        String name,
        String phone,
        String email,
        String pixKey,
        String pixKeyType,
        String preferenceTheme,
        WorkHourRequest workHour
) {
    public record WorkHourRequest(
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ){}
}

