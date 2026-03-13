package com.example.studycore.infrastructure.api.controllers.levelprofile.request;

import java.util.List;

public record UpdateLevelProfileRequest(
        String name,
        String icon,
        String description,
        List<CreateLevelFolderRequest> folders
) {
}

