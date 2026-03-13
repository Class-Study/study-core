package com.example.studycore.application.usecase.folder.input;

import java.util.UUID;

public record CreateFolderInput(
        UUID teacherId,
        UUID studentId,
        String name,
        Integer position
) {
}

