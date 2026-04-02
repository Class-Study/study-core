package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record GetStudentScheduleResponse(
        String studentName,
        List<String> classDays,
        LocalTime classTime,
        Integer classDuration,
        LocalDate startDate,
        LocalDate endDate,
        String teacherName,
        List<ClassItemResponse> classes
) {
    public record ClassItemResponse(
            UUID id,
            LocalDate date,
            LocalTime time,
            String status,
            String classType,
            boolean isNextClass
    ) {
    }
}
