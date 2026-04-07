package com.example.studycore.application.usecase.levelprofile.output;

import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SubfolderOutput(
        UUID id,
        String name,
        Integer position,
        List<LevelFolderTemplateOutput> templates,
        List<StudyMaterialOutput> studyMaterials,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

