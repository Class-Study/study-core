package com.example.studycore.infrastructure.api.controllers.levelprofile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLevelFolderRequest(
        @NotBlank String name,
        @NotNull Integer position,
        @NotNull Integer initialFiles
) {
}

