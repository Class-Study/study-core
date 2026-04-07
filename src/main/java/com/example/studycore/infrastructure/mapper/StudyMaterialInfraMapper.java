package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.model.enums.StudyMaterialType;
import com.example.studycore.domain.model.enums.SubfolderType;
import com.example.studycore.infrastructure.api.controllers.studymaterial.response.StudyMaterialResponse;
import com.example.studycore.infrastructure.persistence.studymaterial.StudyMaterialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudyMaterialInfraMapper {

    StudyMaterialInfraMapper INSTANCE = Mappers.getMapper(StudyMaterialInfraMapper.class);

    default StudyMaterial fromEntity(StudyMaterialEntity entity) {
        if (entity == null) return null;
        final var subfolderType = entity.getSubfolderType() != null
                ? SubfolderType.valueOf(entity.getSubfolderType()) : null;
        return StudyMaterial.with(
                entity.getId(),
                entity.getLevelFolderId(),
                subfolderType,
                entity.getSubfolderId(),
                entity.getTitle(),
                StudyMaterialType.valueOf(entity.getMaterialType()),
                entity.getUrl(),
                entity.getConvertedHtml(),
                entity.getOriginalFilename(),
                entity.getDescription(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default StudyMaterialEntity toEntity(StudyMaterial material) {
        if (material == null) return null;
        final var entity = new StudyMaterialEntity();
        entity.setId(material.getId());
        entity.setLevelFolderId(material.getLevelFolderId());
        entity.setSubfolderId(material.getSubfolderId());
        entity.setSubfolderType(material.getSubfolderType() != null ? material.getSubfolderType().name() : null);
        entity.setTitle(material.getTitle());
        entity.setMaterialType(material.getType().name());
        entity.setUrl(material.getUrl());
        entity.setConvertedHtml(material.getConvertedHtml());
        entity.setOriginalFilename(material.getOriginalFilename());
        entity.setDescription(material.getDescription());
        entity.setCreatedBy(material.getCreatedBy());
        entity.setCreatedAt(material.getCreatedAt());
        entity.setUpdatedAt(material.getUpdatedAt());
        return entity;
    }

    StudyMaterialResponse toResponse(StudyMaterialOutput output);
}
