package com.example.studycore.application.usecase.levelfoldertemplate.input;

import java.util.UUID;

public record CreateLevelFolderTemplateInput(
        UUID levelFolderId,
        UUID levelProfileId,
        UUID teacherId,
        String title,
        String type,
        String originalFilename,
        String convertedHtml,
        Boolean propagateToStudents
) {}

