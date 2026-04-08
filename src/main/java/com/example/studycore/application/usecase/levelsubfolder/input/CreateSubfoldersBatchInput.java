package com.example.studycore.application.usecase.levelsubfolder.input;

import java.util.List;
import java.util.UUID;

public record CreateSubfoldersBatchInput(
        UUID levelProfileId,
        UUID levelFolderId,
        List<BatchSubfolderInput> subfolders,
        UUID createdBy
) {}

