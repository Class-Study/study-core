package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.studentnote.request.CreateStudentNoteRequest;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.StudentNoteResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/students")
@SecurityRequirement(name = "bearerAuth")
public interface StudentNoteApi {

    @GetMapping("/{id}/notes")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<List<StudentNoteResponse>> listNotes(@PathVariable UUID id);

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<StudentNoteResponse> createNote(@PathVariable UUID id, @RequestBody @Valid CreateStudentNoteRequest request);
}

