package com.example.studycore.infrastructure.api.controllers.preferences.request;

import com.example.studycore.domain.model.enums.ThemePreference;
import jakarta.validation.constraints.NotNull;

public record UpdateThemeRequest(
        @NotNull ThemePreference preferenceTheme
) {
}

