package com.example.studycore.application.usecase.activity.input;

import java.util.UUID;

public record CreateActivityInput(
        UUID teacherId,
        UUID folderId,
        String title,
        String type,
        String convertedHtml
) {
}

