package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetStudentWorkspaceFoldersResponse(
        List<FolderItem> folders,
        OffsetDateTime createdAt
) {

    public record FolderItem(
            UUID id,
            String name,
            Integer position,
            Integer initialFiles,
            List<SubfolderItem> subfolders
    ) {}

    public record SubfolderItem(
            UUID id,
            String name,
            Integer position,
            List<ActivityItem> exercises,
            List<StudyMaterialItem> studyMaterials,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    public record ActivityItem(
            UUID id,
            UUID subfolderId,
            String title,
            String type,
            String convertedHtml,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    public record StudyMaterialItem(
            UUID id,
            UUID levelFolderId,
            UUID subfolderId,
            String title,
            String type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description,
            UUID createdBy,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
}
