package com.example.studycore.application.usecase.auth.input;

import com.example.studycore.domain.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginInput(
        @NotBlank @Email String email,
        @NotBlank String password,
        String userAgent,
        @NotNull UserRole expectedRole
) {
}
