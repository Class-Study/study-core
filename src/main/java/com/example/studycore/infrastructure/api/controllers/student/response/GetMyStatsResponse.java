package com.example.studycore.infrastructure.api.controllers.student.response;

public record GetMyStatsResponse(
        Integer activitiesCompleted,
        Integer activitiesTotal,
        Integer classesThisMonth,
        Integer classesTotal,
        Integer overallProgress
) {
}

