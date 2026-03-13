package com.example.studycore.application.usecase.levelprofile.input;

import java.util.List;
import java.util.UUID;

public record CreateLevelProfileInput(
        UUID teacherId,
        String name,
        String code,
        String icon,
        String description,
        List<CreateLevelFolderInput> folders
) {
}

