package com.example.studycore.infrastructure.api.controllers.levelfoldertemplate;

import com.example.studycore.application.usecase.levelfoldertemplate.CreateLevelFolderTemplateUseCase;
import com.example.studycore.application.usecase.levelfoldertemplate.DeleteLevelFolderTemplateUseCase;
import com.example.studycore.application.usecase.levelfoldertemplate.ListLevelFolderTemplatesUseCase;
import com.example.studycore.infrastructure.api.LevelFolderTemplateApi;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request.CreateLevelFolderTemplateRequest;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import com.example.studycore.infrastructure.mapper.LevelFolderTemplateInfraMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LevelFolderTemplateController implements LevelFolderTemplateApi {

    private static final LevelFolderTemplateInfraMapper MAPPER = LevelFolderTemplateInfraMapper.INSTANCE;

    private final CreateLevelFolderTemplateUseCase createLevelFolderTemplateUseCase;
    private final ListLevelFolderTemplatesUseCase listLevelFolderTemplatesUseCase;
    private final DeleteLevelFolderTemplateUseCase deleteLevelFolderTemplateUseCase;

    @Override
    public ResponseEntity<LevelFolderTemplateResponse> create(
            UUID profileId,
            UUID folderId,
            CreateLevelFolderTemplateRequest request
    ) {
        final var input = MAPPER.toCreateInput(profileId, folderId, getAuthenticatedUserId(), request);
        final var output = createLevelFolderTemplateUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(MAPPER.toResponse(output));
    }

    @Override
    public ResponseEntity<List<LevelFolderTemplateResponse>> list(UUID profileId, UUID folderId) {
        final var output = listLevelFolderTemplatesUseCase.execute(profileId, folderId, getAuthenticatedUserId());
        return ResponseEntity.ok(output.stream().map(MAPPER::toResponse).toList());
    }

    @Override
    public ResponseEntity<Void> delete(UUID profileId, UUID folderId, UUID templateId) {
        deleteLevelFolderTemplateUseCase.execute(profileId, folderId, templateId, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

