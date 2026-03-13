package com.example.studycore.infrastructure.api.controllers.activity.request;

import jakarta.validation.constraints.NotBlank;

public record CreateActivityRequest(
        @NotBlank String title,
        @NotBlank String type
) {
}

