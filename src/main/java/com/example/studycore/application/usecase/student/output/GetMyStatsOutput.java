package com.example.studycore.application.usecase.student.output;

public record GetMyStatsOutput(
        Integer activitiesCompleted,
        Integer activitiesTotal,
        Integer classesThisMonth,
        Integer classesTotal,
        Integer overallProgress
) {
}

