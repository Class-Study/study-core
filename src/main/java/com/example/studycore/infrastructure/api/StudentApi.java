package com.example.studycore.infrastructure.api;

import com.example.studycore.application.usecase.student.output.SearchStudentOutput;
import com.example.studycore.infrastructure.api.controllers.activity.response.StudentActivityResponse;
import com.example.studycore.infrastructure.api.controllers.student.request.CreateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.request.UpdateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/students")
@SecurityRequirement(name = "bearerAuth")
public interface StudentApi {

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<String> create(@RequestBody @Valid CreateStudentRequest request);

    @GetMapping("/availability")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<EvaluatedDaysResponse> evaluateAvailability(
            @RequestParam String days,
            @RequestParam Integer durationMin,
            @RequestParam LocalDate startDate,
            @RequestParam LocalTime classTime,
            @RequestParam Integer contractMonths
    );

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<ListStudentsResponse> list();

    @GetMapping("/search")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<List<SearchStudentOutput>> search(@RequestParam(name = "q") String q);

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
