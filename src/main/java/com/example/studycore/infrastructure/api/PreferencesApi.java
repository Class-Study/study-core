package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.preferences.request.UpdateThemeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Preferences", description = "User preference endpoints")
@RequestMapping("/preferences")
public interface PreferencesApi {

    @Operation(
            summary = "Update theme preference",
            description = "Updates the authenticated user's theme preference. Accepts 'light' or 'dark'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Theme updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid theme value"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/theme")
    ResponseEntity<Void> updateTheme(@Valid @RequestBody UpdateThemeRequest request);
}

