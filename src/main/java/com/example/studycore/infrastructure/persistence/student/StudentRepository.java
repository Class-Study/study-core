package com.example.studycore.infrastructure.persistence.student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {
    Optional<StudentEntity> findById(UUID id);

    List<StudentEntity> findByTeacherId(UUID teacherId);
}

