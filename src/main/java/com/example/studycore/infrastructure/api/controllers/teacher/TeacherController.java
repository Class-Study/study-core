package com.example.studycore.infrastructure.api.controllers.teacher;

import com.example.studycore.application.usecase.teacher.*;
import com.example.studycore.infrastructure.api.TeacherApi;
import com.example.studycore.infrastructure.api.controllers.teacher.request.CreateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.request.UpdateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherConfigResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.ListTeachersResponse;
import com.example.studycore.infrastructure.mapper.TeacherInfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.studycore.infrastructure.security.SecurityUtils.getAuthenticatedUserId;

@RestController
@RequiredArgsConstructor
public class TeacherController implements TeacherApi {

    private static final TeacherInfraMapper TEACHER_INFRA_MAPPER = TeacherInfraMapper.INSTANCE;

    private final CreateTeacherUseCase createTeacherUseCase;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;
    private final ListTeachersUseCase listTeachersUseCase;
    private final UpdateTeacherUseCase updateTeacherUseCase;
    private final BlockTeacherUseCase blockTeacherUseCase;
    private final GetTeacherConfigUserCase getTeacherConfigUserCase;

    @Override
    public ResponseEntity<GetTeacherResponse> create(CreateTeacherRequest request) {
        final var input = TEACHER_INFRA_MAPPER.toCreateTeacherInput(request);
        final var output = createTeacherUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TEACHER_INFRA_MAPPER.toGetTeacherResponse(output));
    }

    @Override
    public ResponseEntity<ListTeachersResponse> list() {
        final var output = listTeachersUseCase.execute();
        return ResponseEntity.ok(TEACHER_INFRA_MAPPER.toListTeachersResponse(output));
    }

    @Override
    public ResponseEntity<GetTeacherResponse> getById(UUID id) {
        final var output = getTeacherByIdUseCase.execute(id);
        return ResponseEntity.ok(TEACHER_INFRA_MAPPER.toGetTeacherResponse(output));
    }

    @Override
    public ResponseEntity<GetTeacherConfigResponse> getConfigById() {

        final var id  = getAuthenticatedUserId();

        final var output = getTeacherConfigUserCase.execute(id);

        return ResponseEntity.ok(TEACHER_INFRA_MAPPER.toGetTeacherConfigResponse(output));
    }

    @Override
    public ResponseEntity<Void> update(UpdateTeacherRequest request) {

        final var id = getAuthenticatedUserId();

        final var input = TEACHER_INFRA_MAPPER.toUpdateTeacherInput(id, request);

        updateTeacherUseCase.execute(input);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> block(UUID id) {
        blockTeacherUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
