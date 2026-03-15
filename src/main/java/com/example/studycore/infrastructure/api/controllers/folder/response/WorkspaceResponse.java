package com.example.studycore.infrastructure.api.controllers.folder.response;

import java.util.List;
import java.util.UUID;

public record WorkspaceResponse(
        UUID studentId,
        List<WorkspaceFolderResponse> folders
) {
    public record WorkspaceFolderResponse(
            UUID id,
            String name,
            Integer position,
            List<WorkspaceActivityResponse> activities
    ) {}

    public record WorkspaceActivityResponse(
            UUID id,
            String title,
            String type,
            String contentHtml,
            UUID folderId,
            String createdAt
    ) {}
}

