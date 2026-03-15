package com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request;

import jakarta.validation.constraints.NotBlank;

public record CreateLevelFolderTemplateRequest(
        @NotBlank String title,
        @NotBlank String type,
        String originalFilename,
        @NotBlank String convertedHtml,
        Boolean propagateToStudents
) {}

