package com.example.studycore.infrastructure.api.controllers.activity.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MoveActivityRequest(
        @NotNull(message = "targetFolderId não pode ser nulo")
        UUID targetFolderId
) {
}

