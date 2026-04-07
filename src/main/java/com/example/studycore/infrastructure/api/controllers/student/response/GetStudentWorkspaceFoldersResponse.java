package com.example.studycore.infrastructure.api.controllers.student.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetStudentWorkspaceFoldersResponse(
        List<LevelFolderItem> folders,
        OffsetDateTime createdAt
) {

    public record LevelFolderItem(
            UUID id,
            String name,
            Integer position,
            Integer initialFiles,
            List<TemplateItem> templates,
            List<LevelSubfolderItem> subfolders
    ) {}

    public record LevelSubfolderItem(
            UUID id,
            String name,
            Integer position,
            List<TemplateItem> templates,
            List<StudyMaterialItem> studyMaterials,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    public record TemplateItem(
            UUID id,
            UUID levelFolderId,
            UUID subfolderId,
            String title,
            String type,
            String originalFilename,
            String convertedHtml,
            OffsetDateTime createdAt
    ) {}

    public record StudyMaterialItem(
            UUID id,
            UUID levelFolderId,
            UUID subfolderId,
            String subfolderType,
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

