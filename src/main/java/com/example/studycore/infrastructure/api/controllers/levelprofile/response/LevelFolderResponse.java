package com.example.studycore.infrastructure.api.controllers.levelprofile.response;

import java.util.UUID;

public record LevelFolderResponse(
        UUID id,
        String name,
        Integer position,
        Integer initialFiles
) {
}

