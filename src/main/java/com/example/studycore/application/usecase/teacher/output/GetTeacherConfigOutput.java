package com.example.studycore.application.usecase.teacher.output;

import java.time.LocalTime;

public record GetTeacherConfigOutput(
        String pixKey,
        String pixKeyType,
        LocalTime startTimeMorning,
        LocalTime endTimeMorning,
        LocalTime startTimeAfternoon,
        LocalTime endTimeAfternoon
) {
}
