package com.example.studycore.infrastructure.api.controllers.student;

import com.example.studycore.application.usecase.student.BlockStudentUseCase;
import com.example.studycore.application.usecase.student.CreateStudentUseCase;
import com.example.studycore.application.usecase.student.GetStudentByIdUseCase;
import com.example.studycore.application.usecase.student.ListStudentsUseCase;
import com.example.studycore.application.usecase.student.UpdateStudentUseCase;
import com.example.studycore.infrastructure.api.StudentApi;
import com.example.studycore.infrastructure.api.controllers.student.request.CreateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.request.UpdateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.ListStudentsResponse;
import com.example.studycore.infrastructure.mapper.StudentInfraMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentApi {

    private static final StudentInfraMapper STUDENT_INFRA_MAPPER = StudentInfraMapper.INSTANCE;

    private final CreateStudentUseCase createStudentUseCase;
    private final GetStudentByIdUseCase getStudentByIdUseCase;
    private final ListStudentsUseCase listStudentsUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final BlockStudentUseCase blockStudentUseCase;

    @Override
    public ResponseEntity<GetStudentResponse> create(CreateStudentRequest request) {
        final var teacherId = getAuthenticatedUserId();
        final var input = STUDENT_INFRA_MAPPER.toCreateStudentInput(teacherId, request);
        final var output = createStudentUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(STUDENT_INFRA_MAPPER.toGetStudentResponse(output));
    }

    @Override
    public ResponseEntity<ListStudentsResponse> list() {
        final var output = listStudentsUseCase.execute(getAuthenticatedUserId());
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toListStudentsResponse(output));
    }

    @Override
    public ResponseEntity<GetStudentResponse> getById(UUID id) {
        final var output = getStudentByIdUseCase.execute(id, getAuthenticatedUserId());
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toGetStudentResponse(output));
    }

    @Override
    public ResponseEntity<GetStudentResponse> update(UUID id, UpdateStudentRequest request) {
        final var teacherId = getAuthenticatedUserId();
        final var input = STUDENT_INFRA_MAPPER.toUpdateStudentInput(id, teacherId, request);
        final var output = updateStudentUseCase.execute(input);
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toGetStudentResponse(output));
    }

    @Override
    public ResponseEntity<Void> block(UUID id) {
        blockStudentUseCase.execute(id, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}

