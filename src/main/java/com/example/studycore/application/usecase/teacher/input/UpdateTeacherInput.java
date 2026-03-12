package com.example.studycore.application.usecase.teacher.input;

import java.util.UUID;

public record UpdateTeacherInput(
        UUID id,
        String name,
        String phone,
        String avatarUrl
) {
}
