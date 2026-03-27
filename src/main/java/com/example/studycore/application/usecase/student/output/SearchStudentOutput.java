package com.example.studycore.application.usecase.student.output;

import java.util.UUID;

public record SearchStudentOutput(
        UUID id,
        String name
) {
}

