package com.example.studycore.infrastructure.api.controllers.teacher;

import com.example.studycore.application.usecase.teacher.BlockTeacherUseCase;
import com.example.studycore.application.usecase.teacher.CreateTeacherUseCase;
import com.example.studycore.application.usecase.teacher.GetTeacherByIdUseCase;
import com.example.studycore.application.usecase.teacher.ListTeachersUseCase;
import com.example.studycore.application.usecase.teacher.UpdateTeacherUseCase;
import com.example.studycore.infrastructure.api.TeacherApi;
import com.example.studycore.infrastructure.api.controllers.teacher.request.CreateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.request.UpdateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.ListTeachersResponse;
import com.example.studycore.infrastructure.mapper.TeacherInfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TeacherController implements TeacherApi {

    private static final TeacherInfraMapper TEACHER_INFRA_MAPPER = TeacherInfraMapper.INSTANCE;

    private final CreateTeacherUseCase createTeacherUseCase;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;
    private final ListTeachersUseCase listTeachersUseCase;
    private final UpdateTeacherUseCase updateTeacherUseCase;
    private final BlockTeacherUseCase blockTeacherUseCase;

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
    public ResponseEntity<GetTeacherResponse> update(UUID id, UpdateTeacherRequest request) {
        final var input = TEACHER_INFRA_MAPPER.toUpdateTeacherInput(id, request);
        final var output = updateTeacherUseCase.execute(input);
        return ResponseEntity.ok(TEACHER_INFRA_MAPPER.toGetTeacherResponse(output));
    }

    @Override
    public ResponseEntity<Void> block(UUID id) {
        blockTeacherUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
