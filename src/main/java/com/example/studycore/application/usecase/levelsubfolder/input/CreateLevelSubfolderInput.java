package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.UUID;

public record CreateLevelSubfolderInput(
        UUID levelProfileId,
        UUID levelFolderId,
        String name,
        Integer position,
        UUID createdBy
) {}

