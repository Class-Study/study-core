package com.example.studycore.infrastructure.api.controllers.preferences;

import com.example.studycore.application.usecase.preferences.UpdateThemeUseCase;
import com.example.studycore.application.usecase.preferences.input.UpdateThemeInput;
import com.example.studycore.infrastructure.api.PreferencesApi;
import com.example.studycore.infrastructure.api.controllers.preferences.request.UpdateThemeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PreferencesController implements PreferencesApi {

    private final UpdateThemeUseCase updateThemeUseCase;

    @Override
    public ResponseEntity<Void> updateTheme(UpdateThemeRequest request) {
        final var userId = getAuthenticatedUserId();
        updateThemeUseCase.execute(new UpdateThemeInput(userId, request.preferenceTheme()));
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
    }
}

