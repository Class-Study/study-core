package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.activity.request.CreateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.MoveActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityContentRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.response.GetActivityResponse;
import com.example.studycore.infrastructure.api.controllers.activity.response.ListActivitiesResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/folders")
public interface ActivityApi {

    @PostMapping("/{folderId}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetActivityResponse> createActivity(@PathVariable UUID folderId, @RequestBody @Valid CreateActivityRequest request);

    @GetMapping("/{folderId}/activities")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ResponseEntity<ListActivitiesResponse> listByFolder(@PathVariable UUID folderId);

    @GetMapping("/activities/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ResponseEntity<GetActivityResponse> getById(@PathVariable UUID id);

    @PatchMapping("/activities/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetActivityResponse> updateActivity(@PathVariable UUID id, @RequestBody @Valid UpdateActivityRequest request);

    @PatchMapping("/activities/{id}/content")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ResponseEntity<GetActivityResponse> updateContent(@PathVariable UUID id, @RequestBody @Valid UpdateActivityContentRequest request);

    @PatchMapping("/activities/{activityId}/move")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ResponseEntity<GetActivityResponse> moveActivity(@PathVariable UUID activityId, @RequestBody @Valid MoveActivityRequest request);

    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> deleteActivity(@PathVariable UUID id);
}

