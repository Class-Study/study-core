package com.example.studycore.infrastructure.api.controllers.student;

import com.example.studycore.application.usecase.activity.ListStudentActivitiesUseCase;
import com.example.studycore.application.usecase.student.*;
import com.example.studycore.application.usecase.student.output.SearchStudentOutput;
import com.example.studycore.infrastructure.api.StudentApi;
import com.example.studycore.infrastructure.api.controllers.activity.response.StudentActivityResponse;
import com.example.studycore.infrastructure.api.controllers.student.request.CreateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.request.UpdateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.*;
import com.example.studycore.infrastructure.mapper.StudentInfraMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final ListStudentActivitiesUseCase listStudentActivitiesUseCase;
    private final GetStudentStatsUseCase getStudentStatsUseCase;
    private final UnblockStudentUseCase unblockStudentUseCase;
    private final SearchStudentsUseCase searchStudentsUseCase;
    private final EvaluateAvailabilityUseCase evaluateAvailabilityUseCase;

    @Override
    public ResponseEntity<String> create(CreateStudentRequest request) {
        final var teacherId = getAuthenticatedUserId();
        final var input = STUDENT_INFRA_MAPPER.toCreateStudentInput(teacherId, request);
        final var provisoryPass = createStudentUseCase.execute(input).provisoryPass();
        return ResponseEntity.status(HttpStatus.CREATED).body(provisoryPass);
    }

    public ResponseEntity<EvaluatedDaysResponse> evaluateAvailability(
            String days,
            Integer durationMin,
            LocalDate startDate,
            LocalTime classTime,
            Integer contractMonths
    ) {
        final var teacherId = getAuthenticatedUserId();
        final var input = STUDENT_INFRA_MAPPER.toEvaluateAvailabilityInput(teacherId, days, durationMin, classTime, startDate, contractMonths);
        final var output = evaluateAvailabilityUseCase.execute(input);
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toEvaluatedDaysResponse(output));
    }

    @Override
    public ResponseEntity<ListStudentsResponse> list() {
        final var output = listStudentsUseCase.execute(getAuthenticatedUserId());
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toListStudentsResponse(output));
    }

    @Override
    public ResponseEntity<List<SearchStudentOutput>> search(String q) {
        final var results = searchStudentsUseCase.execute(q);
        return ResponseEntity.ok(results);
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

    @Override
    public ResponseEntity<Void> unblock(UUID id) {
        unblockStudentUseCase.execute(id, getAuthenticatedUserId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<StudentActivityResponse>> listActivities(UUID id) {
        final var output = listStudentActivitiesUseCase.execute(getAuthenticatedUserId(), id);
        return ResponseEntity.ok(output.stream().map(STUDENT_INFRA_MAPPER::toStudentActivityResponse).toList());
    }

    @Override
    public ResponseEntity<StudentStatsResponse> getStats(UUID id) {
        final var output = getStudentStatsUseCase.execute(getAuthenticatedUserId(), id);
        return ResponseEntity.ok(STUDENT_INFRA_MAPPER.toStudentStatsResponse(output));
    }

    private UUID getAuthenticatedUserId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}
