package com.example.studycore.infrastructure.api.controllers.levelprofile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateLevelProfileRequest(
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String icon,
        String description,
        @NotNull List<CreateLevelFolderRequest> folders
) {
}

