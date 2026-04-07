package com.example.studycore.infrastructure.api.controllers.studymaterial.request;

import com.example.studycore.domain.model.enums.StudyMaterialType;

public record UpdateStudyMaterialRequest(
        String title,
        StudyMaterialType type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description
) {}

