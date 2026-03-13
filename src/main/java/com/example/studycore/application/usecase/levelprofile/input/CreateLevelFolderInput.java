package com.example.studycore.application.usecase.levelprofile.input;

public record CreateLevelFolderInput(
        String name,
        Integer position,
        Integer initialFiles
) {
}

