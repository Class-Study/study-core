package com.example.studycore.infrastructure.persistence.studentsubfolder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentSubfolderRepository extends JpaRepository<StudentSubfolderEntity, UUID> {
    List<StudentSubfolderEntity> findByFolderIdOrderByPositionAsc(UUID folderId);
    Optional<StudentSubfolderEntity> findByFolderIdAndLevelSubfolderId(UUID folderId, UUID levelSubfolderId);
    List<StudentSubfolderEntity> findByLevelSubfolderIdOrderByCreatedAtAsc(UUID levelSubfolderId);
}

