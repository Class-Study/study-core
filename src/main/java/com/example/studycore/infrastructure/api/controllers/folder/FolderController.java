package com.example.studycore.infrastructure.api.controllers.folder;

import com.example.studycore.application.usecase.folder.AssignLevelFoldersUseCase;
import com.example.studycore.application.usecase.folder.CreateFolderUseCase;
import com.example.studycore.application.usecase.folder.DeleteFolderUseCase;
import com.example.studycore.application.usecase.folder.ListFoldersByStudentUseCase;
import com.example.studycore.application.usecase.folder.UpdateFolderUseCase;
import com.example.studycore.infrastructure.api.FolderApi;
import com.example.studycore.infrastructure.api.controllers.folder.request.AssignLevelFoldersRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.CreateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.UpdateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.response.GetFolderResponse;
import com.example.studycore.infrastructure.api.controllers.folder.response.ListFoldersResponse;
import com.example.studycore.infrastructure.mapper.FolderInfraMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FolderController implements FolderApi {

    private static final FolderInfraMapper FOLDER_INFRA_MAPPER = FolderInfraMapper.INSTANCE;

    private final CreateFolderUseCase createFolderUseCase;
    private final AssignLevelFoldersUseCase assignLevelFoldersUseCase;
    private final ListFoldersByStudentUseCase listFoldersByStudentUseCase;
    private final UpdateFolderUseCase updateFolderUseCase;
    private final DeleteFolderUseCase deleteFolderUseCase;

    @Override
    public ResponseEntity<GetFolderResponse> createFolder(UUID studentId, CreateFolderRequest request) {
        final var input = FOLDER_INFRA_MAPPER.toCreateFolderInput(getAuthenticatedUserId(), studentId, request);
        final var output = createFolderUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(FOLDER_INFRA_MAPPER.toGetFolderResponse(output));
    }

    @Override
    public ResponseEntity<ListFoldersResponse> assignFromLevelProfile(UUID studentId, AssignLevelFoldersRequest request) {
        final var input = FOLDER_INFRA_MAPPER.toAssignLevelFoldersInput(getAuthenticatedUserId(), studentId, request);
        final var output = assignLevelFoldersUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(FOLDER_INFRA_MAPPER.toListFoldersResponse(output));
    }

    @Override
    public ResponseEntity<ListFoldersResponse> listByStudent(UUID studentId) {
        final var output = listFoldersByStudentUseCase.execute(getAuthenticatedUserId(), studentId);
        return ResponseEntity.ok(FOLDER_INFRA_MAPPER.toListFoldersResponse(output));
    }

    @Override
    public ResponseEntity<GetFolderResponse> updateFolder(UUID folderId, UpdateFolderRequest request) {
        final var input = FOLDER_INFRA_MAPPER.toUpdateFolderInput(getAuthenticatedUserId(), folderId, request);
        final var output = updateFolderUseCase.execute(input);
        return ResponseEntity.ok(FOLDER_INFRA_MAPPER.toGetFolderResponse(output));
    }

    @Override
    public ResponseEntity<Void> deleteFolder(UUID folderId) {
        deleteFolderUseCase.execute(getAuthenticatedUserId(), folderId);
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

