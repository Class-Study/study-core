package com.example.studycore.application.usecase.activity.input;

import java.util.UUID;

public record UpdateActivityInput(
        UUID teacherId,
        UUID activityId,
        String title,
        String type
) {
}

