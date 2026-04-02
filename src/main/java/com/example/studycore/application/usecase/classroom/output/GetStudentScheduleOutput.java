package com.example.studycore.application.usecase.classroom.output;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record GetStudentScheduleOutput(
        String studentName,
        List<String> classDays,
        LocalTime classTime,
        Integer classDuration,
        LocalDate startDate,
        LocalDate endDate,
        String teacherName,
        List<ClassItemOutput> classes
) {
    public record ClassItemOutput(
            UUID id,
            LocalDate date,
            LocalTime time,
            String status,
            String classType,
            boolean isNextClass
    ) {
    }
}
