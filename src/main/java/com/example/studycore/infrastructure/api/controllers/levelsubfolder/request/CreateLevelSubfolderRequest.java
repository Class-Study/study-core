package com.example.studycore.infrastructure.api.controllers.levelsubfolder.request;

import jakarta.validation.constraints.NotBlank;

public record CreateLevelSubfolderRequest(
        @NotBlank String name,
        Integer position
) {}

