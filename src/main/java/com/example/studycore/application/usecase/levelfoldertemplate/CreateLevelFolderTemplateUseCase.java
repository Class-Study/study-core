package com.example.studycore.application.usecase.levelfoldertemplate;

import com.example.studycore.application.mapper.LevelFolderTemplateOutputMapper;
import com.example.studycore.application.usecase.levelfoldertemplate.input.CreateLevelFolderTemplateInput;
import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.LevelFolderTemplate;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateLevelFolderTemplateUseCase {

    private static final LevelFolderTemplateOutputMapper MAPPER = LevelFolderTemplateOutputMapper.INSTANCE;

    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;

    public LevelFolderTemplateOutput execute(CreateLevelFolderTemplateInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.teacherId().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream()
                .noneMatch(f -> f.getId().equals(input.levelFolderId()))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var template = LevelFolderTemplate.create(
                input.levelFolderId(),
                input.title(),
                input.type(),
                input.originalFilename(),
                input.convertedHtml(),
                input.teacherId()
        );

        return MAPPER.toOutput(levelFolderTemplateGateway.save(template));
    }
}

