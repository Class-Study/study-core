package com.example.studycore.application.usecase.auth.input;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenInput(
        @NotBlank String refreshToken
) {
}
