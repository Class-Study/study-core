package com.example.studycore.application.usecase.levelprofile.output;

import java.util.UUID;

public record LevelFolderOutput(
        UUID id,
        String name,
        Integer position,
        Integer initialFiles
) {
}

