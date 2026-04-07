package com.example.studycore.infrastructure.api.controllers.levelsubfolder;

import com.example.studycore.application.usecase.levelsubfolder.CreateLevelSubfolderUseCase;
import com.example.studycore.application.usecase.levelsubfolder.DeleteLevelSubfolderUseCase;
import com.example.studycore.application.usecase.levelsubfolder.ListLevelSubfoldersUseCase;
import com.example.studycore.application.usecase.levelsubfolder.UpdateLevelSubfolderUseCase;
import com.example.studycore.application.usecase.levelsubfolder.input.CreateLevelSubfolderInput;
import com.example.studycore.application.usecase.levelsubfolder.input.UpdateLevelSubfolderInput;
import com.example.studycore.application.usecase.levelsubfolder.output.LevelSubfolderOutput;
import com.example.studycore.infrastructure.api.LevelSubfolderApi;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.CreateLevelSubfolderRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.UpdateLevelSubfolderRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.response.LevelSubfolderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LevelSubfolderController implements LevelSubfolderApi {

    private final CreateLevelSubfolderUseCase createLevelSubfolderUseCase;
    private final UpdateLevelSubfolderUseCase updateLevelSubfolderUseCase;
    private final DeleteLevelSubfolderUseCase deleteLevelSubfolderUseCase;
    private final ListLevelSubfoldersUseCase listLevelSubfoldersUseCase;

    @Override
    public ResponseEntity<List<LevelSubfolderResponse>> list(UUID profileId, UUID folderId) {
        final var output = listLevelSubfoldersUseCase.execute(profileId, folderId, getAuthenticatedUserId());
        return ResponseEntity.ok(output.stream().map(this::toResponse).toList());
    }

    @Override
    public ResponseEntity<LevelSubfolderResponse> create(UUID profileId, UUID folderId, CreateLevelSubfolderRequest request) {
        final var input = new CreateLevelSubfolderInput(
                profileId,
                folderId,
                request.name(),
                request.position(),
                getAuthenticatedUserId()
        );
        final var output = createLevelSubfolderUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(output));
    }

    @Override
    public ResponseEntity<LevelSubfolderResponse> update(UUID profileId, UUID folderId, UUID subfolderId, UpdateLevelSubfolderRequest request) {
        final var input = new UpdateLevelSubfolderInput(
                profileId,
                folderId,
                subfolderId,
                request.name(),
                request.position(),
                getAuthenticatedUserId()
        );
        final var output = updateLevelSubfolderUseCase.execute(input);
        return ResponseEntity.ok(toResponse(output));
    }

    @Override
    public ResponseEntity<Void> delete(UUID profileId, UUID folderId, UUID subfolderId) {
        deleteLevelSubfolderUseCase.execute(profileId, folderId, subfolderId, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private LevelSubfolderResponse toResponse(LevelSubfolderOutput output) {
        return new LevelSubfolderResponse(
                output.id(),
                output.levelFolderId(),
                output.name(),
                output.position(),
                output.createdBy(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

