package com.example.studycore.infrastructure.api.controllers.studymaterial.request;

import com.example.studycore.domain.model.enums.StudyMaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStudyMaterialRequest(
        @NotBlank String title,
        @NotNull StudyMaterialType type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description,
        @NotNull Boolean propagateToStudents
) {}

