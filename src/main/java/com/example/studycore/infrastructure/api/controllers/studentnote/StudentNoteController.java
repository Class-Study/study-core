package com.example.studycore.infrastructure.api.controllers.studentnote;

import com.example.studycore.application.usecase.studentnote.CreateStudentNoteUseCase;
import com.example.studycore.application.usecase.studentnote.ListStudentNotesUseCase;
import com.example.studycore.infrastructure.api.StudentNoteApi;
import com.example.studycore.infrastructure.api.controllers.studentnote.request.CreateStudentNoteRequest;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.StudentNoteResponse;
import com.example.studycore.infrastructure.mapper.StudentNoteInfraMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentNoteController implements StudentNoteApi {

    private static final StudentNoteInfraMapper STUDENT_NOTE_INFRA_MAPPER = StudentNoteInfraMapper.INSTANCE;

    private final ListStudentNotesUseCase listStudentNotesUseCase;
    private final CreateStudentNoteUseCase createStudentNoteUseCase;

    @Override
    public ResponseEntity<List<StudentNoteResponse>> listNotes(UUID id) {
        final var output = listStudentNotesUseCase.execute(getAuthenticatedUserId(), id);
        return ResponseEntity.ok(output.stream().map(STUDENT_NOTE_INFRA_MAPPER::toResponse).toList());
    }

    @Override
    public ResponseEntity<StudentNoteResponse> createNote(UUID id, CreateStudentNoteRequest request) {
        final var input = STUDENT_NOTE_INFRA_MAPPER.toCreateStudentNoteInput(
                getAuthenticatedUserId(),
                id,
                getAuthenticatedUserId(),
                request
        );
        final var output = createStudentNoteUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(STUDENT_NOTE_INFRA_MAPPER.toResponse(output));
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

