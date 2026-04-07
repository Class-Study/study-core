package com.example.studycore.infrastructure.api.controllers.studymaterial;

import com.example.studycore.application.usecase.studymaterial.CreateStudyMaterialUseCase;
import com.example.studycore.application.usecase.studymaterial.DeleteStudyMaterialUseCase;
import com.example.studycore.application.usecase.studymaterial.ListStudyMaterialsBySubfolderUseCase;
import com.example.studycore.application.usecase.studymaterial.UpdateStudyMaterialUseCase;
import com.example.studycore.application.usecase.studymaterial.input.CreateStudyMaterialInput;
import com.example.studycore.application.usecase.studymaterial.input.UpdateStudyMaterialInput;
import com.example.studycore.infrastructure.api.StudyMaterialApi;
import com.example.studycore.infrastructure.api.controllers.studymaterial.request.CreateStudyMaterialRequest;
import com.example.studycore.infrastructure.api.controllers.studymaterial.request.UpdateStudyMaterialRequest;
import com.example.studycore.infrastructure.api.controllers.studymaterial.response.StudyMaterialResponse;
import com.example.studycore.infrastructure.mapper.StudyMaterialInfraMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyMaterialController implements StudyMaterialApi {

    private static final StudyMaterialInfraMapper MAPPER = StudyMaterialInfraMapper.INSTANCE;

    private final CreateStudyMaterialUseCase createStudyMaterialUseCase;
    private final UpdateStudyMaterialUseCase updateStudyMaterialUseCase;
    private final DeleteStudyMaterialUseCase deleteStudyMaterialUseCase;
    private final ListStudyMaterialsBySubfolderUseCase listStudyMaterialsBySubfolderUseCase;

    @Override
    public ResponseEntity<List<StudyMaterialResponse>> list(UUID profileId, UUID folderId, UUID subfolderId) {
        final var output = listStudyMaterialsBySubfolderUseCase.execute(profileId, folderId, subfolderId, getAuthenticatedUserId());
        return ResponseEntity.ok(output.stream().map(MAPPER::toResponse).toList());
    }

    @Override
    public ResponseEntity<StudyMaterialResponse> create(
            UUID profileId, UUID folderId, UUID subfolderId, CreateStudyMaterialRequest request) {
        final var input = new CreateStudyMaterialInput(
                folderId,
                profileId,
                subfolderId,
                request.title(),
                request.type(),
                request.url(),
                request.convertedHtml(),
                request.originalFilename(),
                request.description(),
                request.propagateToStudents(),
                getAuthenticatedUserId()
        );
        final var output = createStudyMaterialUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(MAPPER.toResponse(output));
    }

    @Override
    public ResponseEntity<StudyMaterialResponse> update(
            UUID profileId, UUID folderId, UUID subfolderId, UUID materialId, UpdateStudyMaterialRequest request) {
        final var input = new UpdateStudyMaterialInput(
                materialId,
                folderId,
                profileId,
                request.title(),
                request.type(),
                request.url(),
                request.convertedHtml(),
                request.originalFilename(),
                request.description(),
                getAuthenticatedUserId()
        );
        final var output = updateStudyMaterialUseCase.execute(input);
        return ResponseEntity.ok(MAPPER.toResponse(output));
    }

    @Override
    public ResponseEntity<Void> delete(UUID profileId, UUID folderId, UUID subfolderId, UUID materialId) {
        deleteStudyMaterialUseCase.execute(profileId, folderId, materialId, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
