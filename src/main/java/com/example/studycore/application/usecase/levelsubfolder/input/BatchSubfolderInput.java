package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.List;

public record BatchSubfolderInput(
        String name,
        Integer position,
        Boolean propagateToStudents,
        List<BatchExerciseInput> exercises,
        List<BatchMaterialInput> materials
) {}

