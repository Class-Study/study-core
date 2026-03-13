package com.example.studycore.infrastructure.api.controllers.folder.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFolderRequest(
        @NotBlank String name,
        @NotNull Integer position
) {
}

