package com.example.studycore.infrastructure.persistence.studymaterial;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "study_materials")
public class StudyMaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "level_folder_id")
    private UUID levelFolderId;

    @Column(name = "subfolder_id")
    private UUID subfolderId;

    @Column(name = "subfolder_type", length = 20)
    private String subfolderType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "material_type", nullable = false, length = 20)
    private String materialType;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "converted_html", columnDefinition = "TEXT")
    private String convertedHtml;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        final var now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}

