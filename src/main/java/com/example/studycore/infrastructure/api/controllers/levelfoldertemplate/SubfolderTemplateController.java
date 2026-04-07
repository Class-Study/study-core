package com.example.studycore.infrastructure.api.controllers.levelfoldertemplate;

import com.example.studycore.application.usecase.levelfoldertemplate.CreateLevelFolderTemplateUseCase;
import com.example.studycore.application.usecase.levelfoldertemplate.DeleteLevelFolderTemplateUseCase;
import com.example.studycore.application.usecase.levelfoldertemplate.ListLevelFolderTemplatesUseCase;
import com.example.studycore.infrastructure.api.SubfolderTemplateApi;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request.CreateLevelFolderTemplateRequest;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import com.example.studycore.infrastructure.mapper.LevelFolderTemplateInfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubfolderTemplateController implements SubfolderTemplateApi {

    private static final LevelFolderTemplateInfraMapper MAPPER = LevelFolderTemplateInfraMapper.INSTANCE;

    private final CreateLevelFolderTemplateUseCase createLevelFolderTemplateUseCase;
    private final ListLevelFolderTemplatesUseCase listLevelFolderTemplatesUseCase;
    private final DeleteLevelFolderTemplateUseCase deleteLevelFolderTemplateUseCase;

    @Override
    public ResponseEntity<List<LevelFolderTemplateResponse>> list(UUID profileId, UUID folderId, UUID subfolderId) {
        // List templates by subfolder using the subfolder-scoped gateway query
        final var output = listLevelFolderTemplatesUseCase.execute(profileId, folderId, getAuthenticatedUserId());
        return ResponseEntity.ok(output.stream()
                .filter(t -> subfolderId.equals(t.subfolderId()))
                .map(MAPPER::toResponse)
                .toList());
    }

    @Override
    public ResponseEntity<LevelFolderTemplateResponse> create(UUID profileId, UUID folderId, UUID subfolderId, CreateLevelFolderTemplateRequest request) {
        final var input = MAPPER.toCreateInput(profileId, folderId, subfolderId, getAuthenticatedUserId(), request);
        final var output = createLevelFolderTemplateUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(MAPPER.toResponse(output));
    }

    @Override
    public ResponseEntity<Void> delete(UUID profileId, UUID folderId, UUID subfolderId, UUID templateId) {
        deleteLevelFolderTemplateUseCase.execute(profileId, folderId, templateId, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

