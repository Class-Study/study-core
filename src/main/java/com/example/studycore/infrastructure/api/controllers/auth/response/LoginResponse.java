package com.example.studycore.infrastructure.api.controllers.auth.response;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        UserInfo user
) {
    public record UserInfo(
            UUID id,
            String name,
            String email,
            String role
    ) {
    }
}
