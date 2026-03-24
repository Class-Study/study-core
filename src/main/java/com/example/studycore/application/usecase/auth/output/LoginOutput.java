package com.example.studycore.application.usecase.auth.output;

import com.example.studycore.domain.model.enums.ThemePreference;
import java.util.UUID;

public record LoginOutput(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UUID userId,
        String userName,
        String userEmail,
        String userRole,
        ThemePreference preferenceTheme
) {
}
