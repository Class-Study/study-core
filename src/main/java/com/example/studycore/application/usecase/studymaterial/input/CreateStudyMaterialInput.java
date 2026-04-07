package com.example.studycore.application.usecase.studymaterial.input;

import com.example.studycore.domain.model.enums.StudyMaterialType;

import java.util.UUID;

public record CreateStudyMaterialInput(
        UUID levelFolderId,
        UUID levelProfileId,
        UUID subfolderId,
        String title,
        StudyMaterialType type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description,
        Boolean propagateToStudents,
        UUID createdBy
) {}

