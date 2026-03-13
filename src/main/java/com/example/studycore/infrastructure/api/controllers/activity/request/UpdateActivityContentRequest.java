package com.example.studycore.infrastructure.api.controllers.activity.request;

import jakarta.validation.constraints.NotNull;

public record UpdateActivityContentRequest(
        @NotNull String contentHtml
) {
}

