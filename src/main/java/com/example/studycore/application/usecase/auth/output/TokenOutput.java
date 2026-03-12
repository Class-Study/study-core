package com.example.studycore.application.usecase.auth.output;

public record TokenOutput(
        String accessToken,
        long expiresIn
) {
}
