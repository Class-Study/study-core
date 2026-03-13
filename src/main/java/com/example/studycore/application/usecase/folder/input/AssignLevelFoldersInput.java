package com.example.studycore.application.usecase.folder.input;

import java.util.List;
import java.util.UUID;

public record AssignLevelFoldersInput(
        UUID teacherId,
        UUID studentId,
        List<UUID> levelFolderIds
) {
}

