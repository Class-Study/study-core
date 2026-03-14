package com.example.studycore.infrastructure.persistence.studentnote;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentNoteRepository extends JpaRepository<StudentNoteEntity, UUID> {
    List<StudentNoteEntity> findByStudentIdOrderByCreatedAtDesc(UUID studentId);
}

