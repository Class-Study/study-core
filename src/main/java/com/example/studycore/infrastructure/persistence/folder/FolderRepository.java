package com.example.studycore.infrastructure.persistence.folder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {
    Optional<FolderEntity> findById(UUID id);
    List<FolderEntity> findByStudentIdOrderByPositionAsc(UUID studentId);
}

