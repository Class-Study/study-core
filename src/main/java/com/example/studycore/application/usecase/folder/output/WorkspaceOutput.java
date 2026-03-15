package com.example.studycore.application.usecase.folder.output;

import java.util.List;
import java.util.UUID;

public record WorkspaceOutput(
        UUID studentId,
        List<WorkspaceFolderOutput> folders
) {
    public record WorkspaceFolderOutput(
            UUID id,
            String name,
            Integer position,
            List<WorkspaceActivityOutput> activities
    ) {}

    public record WorkspaceActivityOutput(
            UUID id,
            String title,
            String type,
            String contentHtml,
            UUID folderId,
            String createdAt
    ) {}
}

