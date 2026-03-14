package com.example.studycore.infrastructure.api.controllers.levelprofile.response;

import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import java.util.List;
import java.util.UUID;

public record LevelFolderResponse(
        UUID id,
        String name,
        Integer position,
        Integer initialFiles,
        List<LevelFolderTemplateResponse> templates
) {
}

