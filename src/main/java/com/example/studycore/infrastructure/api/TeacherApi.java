package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.teacher.request.CreateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.request.UpdateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.ListTeachersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Teachers", description = "Teacher management endpoints - Admin only")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/teachers")
public interface TeacherApi {

    @Operation(summary = "Create a new teacher",
               description = "Creates a new teacher with temporary password and sends welcome email")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Teacher created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or email already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<GetTeacherResponse> create(@Valid @RequestBody CreateTeacherRequest request);

    @Operation(summary = "List all teachers",
               description = "Returns a list of all teachers ordered by name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ListTeachersResponse> list();

    @Operation(summary = "Get teacher by ID",
               description = "Returns teacher details by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Teacher found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<GetTeacherResponse> getById(@PathVariable UUID id);

    @Operation(summary = "Update teacher",
               description = "Updates teacher information (name, phone, avatar, timezone)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Teacher updated successfully"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<GetTeacherResponse> update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateTeacherRequest request);

    @Operation(summary = "Block teacher",
               description = "Blocks teacher (logical delete) - teacher cannot login anymore")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Teacher blocked successfully"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> block(@PathVariable UUID id);
}
