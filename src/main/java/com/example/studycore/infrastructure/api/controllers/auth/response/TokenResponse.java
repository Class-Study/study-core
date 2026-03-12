package com.example.studycore.infrastructure.api.controllers.auth.response;

public record TokenResponse(
        String accessToken,
        Long expiresIn
) {
}
