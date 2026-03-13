package com.example.studycore.infrastructure.api.controllers.folder.request;

public record UpdateFolderRequest(
        String name,
        Integer position
) {
}

