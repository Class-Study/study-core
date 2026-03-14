package com.example.studycore.application.usecase.student.output;

public record StudentStatsOutput(
        int activitiesCompleted,
        int activitiesTotal,
        int classesThisMonth,
        int classesTotal,
        int overallProgress
) {
}

