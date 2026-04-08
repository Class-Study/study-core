package com.example.studycore.application.usecase.levelsubfolder.output;

import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record BatchSubfolderOutput(
        UUID id,
        UUID levelFolderId,
        String name,
        Integer position,
        List<LevelFolderTemplateOutput> exercises,
        List<StudyMaterialOutput> materials,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

