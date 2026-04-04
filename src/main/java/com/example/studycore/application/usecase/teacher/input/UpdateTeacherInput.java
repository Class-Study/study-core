package com.example.studycore.application.usecase.teacher.input;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateTeacherInput(
        UUID id,
        String name,
        String phone,
        String email,
        String pixKey,
        String pixKeyType,
        String preferenceTheme,
        WorkHourInput workHour
) {
    public record WorkHourInput(
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ){}
}
