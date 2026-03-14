package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request.CreateLevelFolderTemplateRequest;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/level-profiles/{profileId}/folders/{folderId}/templates")
@SecurityRequirement(name = "bearerAuth")
public interface LevelFolderTemplateApi {

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<LevelFolderTemplateResponse> create(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @RequestBody @Valid CreateLevelFolderTemplateRequest request
    );

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<List<LevelFolderTemplateResponse>> list(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId
    );

    @DeleteMapping("/{templateId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> delete(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID templateId
    );
}

