package com.example.studycore.application.usecase.preferences.input;

import com.example.studycore.domain.model.enums.ThemePreference;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateThemeInput(
        @NotNull UUID userId,
        @NotNull ThemePreference theme
) {
}

