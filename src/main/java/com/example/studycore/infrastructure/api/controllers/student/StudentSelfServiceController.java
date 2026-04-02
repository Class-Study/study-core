package com.example.studycore.infrastructure.api.controllers.student;

import com.example.studycore.application.usecase.activity.GetMyActivitiesUseCase;
import com.example.studycore.application.usecase.billing.GetStudentBillingUseCase;
import com.example.studycore.application.usecase.student.GetMyProfileUseCase;
import com.example.studycore.application.usecase.student.GetMyStatsUseCase;
import com.example.studycore.application.usecase.studentnote.GetMyNotesUseCase;
import com.example.studycore.infrastructure.api.StudentSelfServiceApi;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyActivitiesResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyProfileResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyStatsResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentBillingResponse;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.GetMyNotesResponse;
import com.example.studycore.infrastructure.mapper.StudentMeResponseMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentSelfServiceController implements StudentSelfServiceApi {

    private static final StudentMeResponseMapper MAPPER = StudentMeResponseMapper.INSTANCE;

    private final GetMyProfileUseCase getMyProfileUseCase;
    private final GetMyActivitiesUseCase getMyActivitiesUseCase;
    private final GetMyStatsUseCase getMyStatsUseCase;
    private final GetMyNotesUseCase getMyNotesUseCase;
    private final GetStudentBillingUseCase getStudentBillingUseCase;

    @Override
    public ResponseEntity<GetMyProfileResponse> getMyProfile() {
        final var studentId = getAuthenticatedUserId();
        final var output = getMyProfileUseCase.execute(studentId);
        return ResponseEntity.ok(MAPPER.toGetMyProfileResponse(output));
    }

    @Override
    public ResponseEntity<GetMyActivitiesResponse> getMyActivities() {
        final var studentId = getAuthenticatedUserId();
        final var output = getMyActivitiesUseCase.execute(studentId);
        return ResponseEntity.ok(MAPPER.toGetMyActivitiesResponse(output));
    }

    @Override
    public ResponseEntity<GetMyStatsResponse> getMyStats() {
        final var studentId = getAuthenticatedUserId();
        final var output = getMyStatsUseCase.execute(studentId);
        return ResponseEntity.ok(MAPPER.toGetMyStatsResponse(output));
    }

    @Override
    public ResponseEntity<List<GetMyNotesResponse>> getMyNotes() {
        final var studentId = getAuthenticatedUserId();
        final var output = getMyNotesUseCase.execute(studentId);
        return ResponseEntity.ok(output.stream().map(MAPPER::toGetMyNotesResponse).toList());
    }

    @Override
    public ResponseEntity<GetStudentBillingResponse> getMyBilling() {
        final var studentId = getAuthenticatedUserId();
        final var output = getStudentBillingUseCase.execute(studentId);
        return ResponseEntity.ok(MAPPER.toGetStudentBillingResponse(output));
    }

    private UUID getAuthenticatedUserId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}

