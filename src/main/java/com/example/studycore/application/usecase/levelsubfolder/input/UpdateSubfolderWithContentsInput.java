package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.List;
import java.util.UUID;

public record UpdateSubfolderWithContentsInput(
        UUID levelProfileId,
        UUID levelFolderId,
        UUID subfolderId,
        String name,
        Boolean propagateToStudents,
        List<ExerciseItemInput> exercises,
        List<MaterialItemInput> materials,
        List<UUID> deletedExerciseIds,
        List<UUID> deletedMaterialIds,
        UUID updatedBy
) {}

