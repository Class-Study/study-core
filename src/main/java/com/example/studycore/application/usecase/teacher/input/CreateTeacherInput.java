package com.example.studycore.application.usecase.teacher.input;

public record CreateTeacherInput(
        String name,
        String email,
        String phone,
        String avatarUrl
) {
}
