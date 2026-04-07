package com.example.studycore.application.usecase.studymaterial.input;

import com.example.studycore.domain.model.enums.StudyMaterialType;

import java.util.UUID;

public record UpdateStudyMaterialInput(
        UUID materialId,
        UUID levelFolderId,
        UUID levelProfileId,
        String title,
        StudyMaterialType type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description,
        UUID updatedBy
) {}

