package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.student.response.GetMyActivitiesResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyProfileResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyStatsResponse;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.GetMyNotesResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/students/me")
@SecurityRequirement(name = "bearerAuth")
public interface StudentSelfServiceApi {

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<GetMyProfileResponse> getMyProfile();

    @GetMapping("/activities")
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<GetMyActivitiesResponse> getMyActivities();

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<GetMyStatsResponse> getMyStats();

    @GetMapping("/notes")
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<List<GetMyNotesResponse>> getMyNotes();
}

