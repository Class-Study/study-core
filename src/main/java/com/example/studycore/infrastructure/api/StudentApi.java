package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.activity.response.StudentActivityResponse;
import com.example.studycore.infrastructure.api.controllers.student.request.CreateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.request.UpdateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.ListStudentsResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.StudentStatsResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/students")
@SecurityRequirement(name = "bearerAuth")
public interface StudentApi {

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<String> create(@RequestBody @Valid CreateStudentRequest request);

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<ListStudentsResponse> list();

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetStudentResponse> getById(@PathVariable UUID id);

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetStudentResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateStudentRequest request);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> block(@PathVariable UUID id);

    @PatchMapping("/{id}/unblock")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> unblock(@PathVariable UUID id);

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<List<StudentActivityResponse>> listActivities(@PathVariable UUID id);

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<StudentStatsResponse> getStats(@PathVariable UUID id);
}
