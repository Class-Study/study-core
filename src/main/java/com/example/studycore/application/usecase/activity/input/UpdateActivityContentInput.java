package com.example.studycore.application.usecase.activity.input;

import java.util.UUID;

public record UpdateActivityContentInput(
        UUID authenticatedUserId,
        boolean teacher,
        UUID activityId,
        String contentHtml
) {
}

