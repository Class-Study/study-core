package com.example.studycore.infrastructure.api.controllers.levelprofile;

import com.example.studycore.application.usecase.levelprofile.CreateLevelProfileUseCase;
import com.example.studycore.application.usecase.levelprofile.DeleteLevelProfileUseCase;
import com.example.studycore.application.usecase.levelprofile.GetLevelProfileByIdUseCase;
import com.example.studycore.application.usecase.levelprofile.ListLevelProfilesUseCase;
import com.example.studycore.application.usecase.levelprofile.UpdateLevelProfileUseCase;
import com.example.studycore.infrastructure.api.LevelProfileApi;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.CreateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.UpdateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.GetLevelProfileResponse;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.ListLevelProfilesResponse;
import com.example.studycore.infrastructure.mapper.LevelProfileInfraMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LevelProfileController implements LevelProfileApi {

    private static final LevelProfileInfraMapper LEVEL_PROFILE_INFRA_MAPPER = LevelProfileInfraMapper.INSTANCE;

    private final CreateLevelProfileUseCase createLevelProfileUseCase;
    private final GetLevelProfileByIdUseCase getLevelProfileByIdUseCase;
    private final ListLevelProfilesUseCase listLevelProfilesUseCase;
    private final UpdateLevelProfileUseCase updateLevelProfileUseCase;
    private final DeleteLevelProfileUseCase deleteLevelProfileUseCase;

    @Override
    public ResponseEntity<GetLevelProfileResponse> create(CreateLevelProfileRequest request) {
        final var input = LEVEL_PROFILE_INFRA_MAPPER.toCreateLevelProfileInput(getAuthenticatedUserId(), request);
        final var output = createLevelProfileUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(LEVEL_PROFILE_INFRA_MAPPER.toGetLevelProfileResponse(output));
    }

    @Override
    public ResponseEntity<ListLevelProfilesResponse> list() {
        final var output = listLevelProfilesUseCase.execute(getAuthenticatedUserId());
        return ResponseEntity.ok(LEVEL_PROFILE_INFRA_MAPPER.toListLevelProfilesResponse(output));
    }

    @Override
    public ResponseEntity<GetLevelProfileResponse> getById(UUID id) {
        final var output = getLevelProfileByIdUseCase.execute(id, getAuthenticatedUserId());
        return ResponseEntity.ok(LEVEL_PROFILE_INFRA_MAPPER.toGetLevelProfileResponse(output));
    }

    @Override
    public ResponseEntity<GetLevelProfileResponse> update(UUID id, UpdateLevelProfileRequest request) {
        final var input = LEVEL_PROFILE_INFRA_MAPPER.toUpdateLevelProfileInput(id, getAuthenticatedUserId(), request);
        final var output = updateLevelProfileUseCase.execute(input);
        return ResponseEntity.ok(LEVEL_PROFILE_INFRA_MAPPER.toGetLevelProfileResponse(output));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        deleteLevelProfileUseCase.execute(id, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}

