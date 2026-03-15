package com.example.studycore.infrastructure.api.controllers.activity;

import com.example.studycore.application.usecase.activity.CreateActivityUseCase;
import com.example.studycore.application.usecase.activity.DeleteActivityUseCase;
import com.example.studycore.application.usecase.activity.GetActivityByIdUseCase;
import com.example.studycore.application.usecase.activity.ListActivitiesByFolderUseCase;
import com.example.studycore.application.usecase.activity.MoveActivityUseCase;
import com.example.studycore.application.usecase.activity.UpdateActivityContentUseCase;
import com.example.studycore.application.usecase.activity.UpdateActivityUseCase;
import com.example.studycore.infrastructure.api.ActivityApi;
import com.example.studycore.infrastructure.api.controllers.activity.request.CreateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.MoveActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityContentRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.response.GetActivityResponse;
import com.example.studycore.infrastructure.api.controllers.activity.response.ListActivitiesResponse;
import com.example.studycore.infrastructure.mapper.ActivityInfraMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityController implements ActivityApi {

    private static final ActivityInfraMapper ACTIVITY_INFRA_MAPPER = ActivityInfraMapper.INSTANCE;

    private final CreateActivityUseCase createActivityUseCase;
    private final ListActivitiesByFolderUseCase listActivitiesByFolderUseCase;
    private final GetActivityByIdUseCase getActivityByIdUseCase;
    private final UpdateActivityUseCase updateActivityUseCase;
    private final UpdateActivityContentUseCase updateActivityContentUseCase;
    private final MoveActivityUseCase moveActivityUseCase;
    private final DeleteActivityUseCase deleteActivityUseCase;

    @Override
    public ResponseEntity<GetActivityResponse> createActivity(UUID folderId, CreateActivityRequest request) {
        final var input = ACTIVITY_INFRA_MAPPER.toCreateActivityInput(getAuthenticatedUserId(), folderId, request);
        final var output = createActivityUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(ACTIVITY_INFRA_MAPPER.toGetActivityResponse(output));
    }

    @Override
    public ResponseEntity<ListActivitiesResponse> listByFolder(UUID folderId) {
        final var output = listActivitiesByFolderUseCase.execute(folderId, getAuthenticatedUserId(), isTeacher());
        return ResponseEntity.ok(ACTIVITY_INFRA_MAPPER.toListActivitiesResponse(output));
    }

    @Override
    public ResponseEntity<GetActivityResponse> getById(UUID id) {
        final var output = getActivityByIdUseCase.execute(id, getAuthenticatedUserId(), isTeacher());
        return ResponseEntity.ok(ACTIVITY_INFRA_MAPPER.toGetActivityResponse(output));
    }

    @Override
    public ResponseEntity<GetActivityResponse> updateActivity(UUID id, UpdateActivityRequest request) {
        final var input = ACTIVITY_INFRA_MAPPER.toUpdateActivityInput(getAuthenticatedUserId(), id, request);
        final var output = updateActivityUseCase.execute(input);
        return ResponseEntity.ok(ACTIVITY_INFRA_MAPPER.toGetActivityResponse(output));
    }

    @Override
    public ResponseEntity<GetActivityResponse> updateContent(UUID id, UpdateActivityContentRequest request) {
        final var input = ACTIVITY_INFRA_MAPPER.toUpdateActivityContentInput(getAuthenticatedUserId(), isTeacher(), id, request);
        final var output = updateActivityContentUseCase.execute(input);
        return ResponseEntity.ok(ACTIVITY_INFRA_MAPPER.toGetActivityResponse(output));
    }

    @Override
    public ResponseEntity<GetActivityResponse> moveActivity(UUID activityId, MoveActivityRequest request) {
        final var input = ACTIVITY_INFRA_MAPPER.toMoveActivityInput(getAuthenticatedUserId(), activityId, request);
        final var output = moveActivityUseCase.execute(input);
        return ResponseEntity.ok(ACTIVITY_INFRA_MAPPER.toGetActivityResponse(output));
    }

    @Override
    public ResponseEntity<Void> deleteActivity(UUID id) {
        deleteActivityUseCase.execute(getAuthenticatedUserId(), id);
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    private boolean isTeacher() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> "ROLE_TEACHER".equals(auth.getAuthority()));
    }
}

