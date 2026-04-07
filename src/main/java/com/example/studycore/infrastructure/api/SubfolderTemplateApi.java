package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request.CreateLevelFolderTemplateRequest;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * Templates scoped to a specific subfolder (new endpoint).
 * The legacy endpoint remains at /level-profiles/{profileId}/folders/{folderId}/templates
 */
@RequestMapping("/level-profiles/{profileId}/folders/{folderId}/subfolders/{subfolderId}/templates")
@SecurityRequirement(name = "bearerAuth")
public interface SubfolderTemplateApi {

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<List<LevelFolderTemplateResponse>> list(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId
    );

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<LevelFolderTemplateResponse> create(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @Valid @RequestBody CreateLevelFolderTemplateRequest request
    );

    @DeleteMapping("/{templateId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Void> delete(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @PathVariable UUID templateId
    );
}

