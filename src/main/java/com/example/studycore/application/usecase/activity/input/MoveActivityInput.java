package com.example.studycore.application.usecase.activity.input;

import java.util.UUID;

public record MoveActivityInput(
        UUID teacherId,
        UUID activityId,
        UUID targetFolderId
) {
}

