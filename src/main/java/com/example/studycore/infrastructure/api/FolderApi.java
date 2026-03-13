package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.folder.request.AssignLevelFoldersRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.CreateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.UpdateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.response.GetFolderResponse;
import com.example.studycore.infrastructure.api.controllers.folder.response.ListFoldersResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/students")
public interface FolderApi {

    @PostMapping("/{studentId}/folders")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetFolderResponse> createFolder(@PathVariable UUID studentId, @RequestBody @Valid CreateFolderRequest request);

    @PostMapping("/{studentId}/folders/from-level-profile")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<ListFoldersResponse> assignFromLevelProfile(@PathVariable UUID studentId, @RequestBody @Valid AssignLevelFoldersRequest request);

    @GetMapping("/{studentId}/folders")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<ListFoldersResponse> listByStudent(@PathVariable UUID studentId);

    @PatchMapping("/folders/{folderId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<GetFolderResponse> updateFolder(@PathVariable UUID folderId, @RequestBody @Valid UpdateFolderRequest request);

    @DeleteMapping("/folders/{folderId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> deleteFolder(@PathVariable UUID folderId);
}

