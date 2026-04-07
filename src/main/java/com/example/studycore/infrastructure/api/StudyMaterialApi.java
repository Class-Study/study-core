package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.studymaterial.request.CreateStudyMaterialRequest;
import com.example.studycore.infrastructure.api.controllers.studymaterial.request.UpdateStudyMaterialRequest;
import com.example.studycore.infrastructure.api.controllers.studymaterial.response.StudyMaterialResponse;
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

@RequestMapping("/level-profiles/{profileId}/folders/{folderId}/subfolders/{subfolderId}/materials")
@SecurityRequirement(name = "bearerAuth")
public interface StudyMaterialApi {

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<List<StudyMaterialResponse>> list(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId
    );

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<StudyMaterialResponse> create(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @Valid @RequestBody CreateStudyMaterialRequest request
    );

    @PatchMapping("/{materialId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<StudyMaterialResponse> update(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @PathVariable UUID materialId,
            @Valid @RequestBody UpdateStudyMaterialRequest request
    );

    @DeleteMapping("/{materialId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Void> delete(
            @PathVariable UUID profileId,
            @PathVariable UUID folderId,
            @PathVariable UUID subfolderId,
            @PathVariable UUID materialId
    );
}
