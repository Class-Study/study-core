package com.example.studycore.infrastructure.api.controllers.teacher.request;

public record UpdateTeacherRequest(
        String name,
        String phone,
        String avatarUrl
) {
}
