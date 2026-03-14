package com.example.studycore.infrastructure.api.controllers.student.response;

public record StudentStatsResponse(
        int activitiesCompleted,
        int activitiesTotal,
        int classesThisMonth,
        int classesTotal,
        int overallProgress
) {
}

