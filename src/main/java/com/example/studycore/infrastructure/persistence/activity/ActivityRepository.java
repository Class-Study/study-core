package com.example.studycore.infrastructure.persistence.activity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    Optional<ActivityEntity> findById(UUID id);
    List<ActivityEntity> findByFolderIdOrderByCreatedAtAsc(UUID folderId);
}

