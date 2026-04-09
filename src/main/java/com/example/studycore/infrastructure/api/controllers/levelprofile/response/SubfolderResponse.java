package com.example.studycore.infrastructure.api.controllers.levelprofile.response;

import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import com.example.studycore.infrastructure.api.controllers.studymaterial.response.StudyMaterialResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SubfolderResponse(
        UUID id,
        String name,
        Integer position,
        List<LevelFolderTemplateResponse> exercises,
        List<StudyMaterialResponse> studyMaterials,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

