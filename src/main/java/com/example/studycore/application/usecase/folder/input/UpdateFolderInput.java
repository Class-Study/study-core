package com.example.studycore.application.usecase.folder.input;

import java.util.UUID;

public record UpdateFolderInput(
        UUID teacherId,
        UUID folderId,
        String name,
        Integer position
) {
}

