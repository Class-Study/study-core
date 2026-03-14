package com.example.studycore.application.usecase.studentnote.input;

import java.util.UUID;

public record CreateStudentNoteInput(
        UUID teacherId,
        UUID studentId,
        UUID authorId,
        String type,
        String content
) {
}

