package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.levelfoldertemplate.input.CreateLevelFolderTemplateInput;
import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.model.LevelFolderTemplate;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.request.CreateLevelFolderTemplateRequest;
import com.example.studycore.infrastructure.api.controllers.levelfoldertemplate.response.LevelFolderTemplateResponse;
import com.example.studycore.infrastructure.persistence.levelfoldertemplate.LevelFolderTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface LevelFolderTemplateInfraMapper {

    LevelFolderTemplateInfraMapper INSTANCE = Mappers.getMapper(LevelFolderTemplateInfraMapper.class);

    default CreateLevelFolderTemplateInput toCreateInput(
            UUID levelProfileId,
            UUID levelFolderId,
            UUID subfolderId,
            UUID teacherId,
            CreateLevelFolderTemplateRequest request
    ) {
        final boolean propagate = request.propagateToStudents() != null && request.propagateToStudents();
        return new CreateLevelFolderTemplateInput(
                levelFolderId,
                levelProfileId,
                subfolderId,
                teacherId,
                request.title(),
                request.type(),
                request.originalFilename(),
                request.convertedHtml(),
                propagate
        );
    }

    default LevelFolderTemplate fromEntity(LevelFolderTemplateEntity entity) {
        if (entity == null) return null;
        return LevelFolderTemplate.with(
                entity.getId(),
                entity.getLevelFolderId(),
                entity.getSubfolderId(),
                entity.getTitle(),
                entity.getType(),
                entity.getOriginalFilename(),
                entity.getConvertedHtml(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "levelFolderId", source = "levelFolderId")
    @Mapping(target = "subfolderId", source = "subfolderId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "originalFilename", source = "originalFilename")
    @Mapping(target = "convertedHtml", source = "convertedHtml")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    LevelFolderTemplateEntity toEntity(LevelFolderTemplate template);

    LevelFolderTemplateResponse toResponse(LevelFolderTemplateOutput output);
}
