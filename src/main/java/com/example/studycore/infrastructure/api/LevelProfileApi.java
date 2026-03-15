package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.levelprofile.request.CreateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.UpdateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.GetLevelProfileResponse;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.ListLevelProfilesResponse;
import jakarta.validation.Valid;
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

@RequestMapping("/level-profiles")
public interface LevelProfileApi {

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetLevelProfileResponse> create(@RequestBody @Valid CreateLevelProfileRequest request);

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<ListLevelProfilesResponse> list();

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetLevelProfileResponse> getById(@PathVariable UUID id);

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetLevelProfileResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateLevelProfileRequest request);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> delete(@PathVariable UUID id);
}

