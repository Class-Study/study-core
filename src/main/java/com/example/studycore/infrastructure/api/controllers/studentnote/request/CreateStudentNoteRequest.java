package com.example.studycore.infrastructure.api.controllers.studentnote.request;

import jakarta.validation.constraints.NotBlank;

public record CreateStudentNoteRequest(
        @NotBlank String type,
        @NotBlank String content
) {
}

