package com.example.studycore.application.usecase.levelprofile.input;

import java.util.List;
import java.util.UUID;

public record UpdateLevelProfileInput(
        UUID id,
        UUID teacherId,
        String name,
        String icon,
        String description,
        List<CreateLevelFolderInput> folders
) {
}

