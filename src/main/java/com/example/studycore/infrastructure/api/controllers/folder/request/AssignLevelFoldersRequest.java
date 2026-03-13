package com.example.studycore.infrastructure.api.controllers.folder.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record AssignLevelFoldersRequest(
        @NotNull @NotEmpty List<UUID> levelFolderIds
) {
}

