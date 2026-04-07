package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.model.StudyMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudyMaterialOutputMapper {

    StudyMaterialOutputMapper INSTANCE = Mappers.getMapper(StudyMaterialOutputMapper.class);

    default StudyMaterialOutput toOutput(StudyMaterial material) {
        if (material == null) return null;
        return new StudyMaterialOutput(
                material.getId(),
                material.getLevelFolderId(),
                material.getSubfolderId(),
                material.getSubfolderType() != null ? material.getSubfolderType().name() : null,
                material.getTitle(),
                material.getType() != null ? material.getType().name() : null,
                material.getUrl(),
                material.getConvertedHtml(),
                material.getOriginalFilename(),
                material.getDescription(),
                material.getCreatedBy(),
                material.getCreatedAt(),
                material.getUpdatedAt()
        );
    }
}
