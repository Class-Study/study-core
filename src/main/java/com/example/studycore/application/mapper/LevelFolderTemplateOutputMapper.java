package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.model.LevelFolderTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LevelFolderTemplateOutputMapper {

    LevelFolderTemplateOutputMapper INSTANCE = Mappers.getMapper(LevelFolderTemplateOutputMapper.class);

    LevelFolderTemplateOutput toOutput(LevelFolderTemplate template);
}

