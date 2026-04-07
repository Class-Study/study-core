package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.UUID;

public record UpdateLevelSubfolderInput(
        UUID levelProfileId,
        UUID levelFolderId,
        UUID subfolderId,
        String name,
        Integer position,
        UUID updatedBy
) {}

