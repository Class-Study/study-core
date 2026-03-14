package com.example.studycore.domain.port;

import com.example.studycore.domain.model.StudentNote;
import java.util.List;
import java.util.UUID;

public interface StudentNoteGateway {
    List<StudentNote> findAllByStudentId(UUID studentId);
    StudentNote save(StudentNote note);
}

