package com.example.studycore.infrastructure.persistence.studymaterial;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterialEntity, UUID> {

    List<StudyMaterialEntity> findByLevelFolderIdAndSubfolderTypeOrderByCreatedAtAsc(
            UUID levelFolderId,
            String subfolderType
    );

    List<StudyMaterialEntity> findBySubfolderIdOrderByCreatedAtAsc(UUID subfolderId);

    List<StudyMaterialEntity> findByLevelFolderIdOrderByCreatedAtAsc(UUID levelFolderId);
}

