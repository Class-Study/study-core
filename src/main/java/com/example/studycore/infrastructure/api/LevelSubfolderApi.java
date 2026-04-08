package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.CreateLevelSubfolderRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.CreateSubfoldersBatchRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.UpdateLevelSubfolderRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.UpdateSubfolderWithContentsRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.response.LevelSubfolderResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/level-profiles/{profileId}/folders/{folderId}/subfolders")
@SecurityRequirement(name = "bearerAuth")
public interface LevelSubfolderApi {

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<List<LevelSubfolderResponse>> list(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId
    );

    // NOTE: /batch must be declared before /{subfolderId} so Spring does not treat "batch" as a UUID
    @PostMapping("/batch")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Void> createBatch(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @Valid @RequestBody CreateSubfoldersBatchRequest request
    );

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<LevelSubfolderResponse> create(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @Valid @RequestBody CreateLevelSubfolderRequest request
    );

    @PutMapping("/{subfolderId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Void> updateWithContents(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @Valid @RequestBody UpdateSubfolderWithContentsRequest request
    );

    @PatchMapping("/{subfolderId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<LevelSubfolderResponse> update(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @Valid @RequestBody UpdateLevelSubfolderRequest request
    );

    @DeleteMapping("/{subfolderId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Void> delete(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId
    );
}

