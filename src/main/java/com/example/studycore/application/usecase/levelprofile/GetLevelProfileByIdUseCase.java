package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLevelProfileByIdUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;

    public GetLevelProfileOutput execute(UUID id, UUID teacherId) {
        final var profile = levelProfileGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!profile.isSystem() && !teacherId.equals(profile.getCreatedBy())) {
            throw new BusinessException("Sem acesso a este perfil de nível.");
        }

        final var output = MAPPER.toGetLevelProfileOutput(profile);

        final var foldersWithTemplates = output.folders().stream()
                .map(folder -> new LevelFolderOutput(
                        folder.id(),
                        folder.name(),
                        folder.position(),
                        folder.initialFiles(),
                        levelFolderTemplateGateway.findAllByFolderId(folder.id()).stream()
                                .map(template -> new LevelFolderTemplateOutput(
                                        template.getId(),
                                        template.getLevelFolderId(),
                                        template.getTitle(),
                                        template.getType(),
                                        template.getOriginalFilename(),
                                        template.getCreatedAt()
                                ))
                                .toList()
                ))
                .toList();

        return new GetLevelProfileOutput(
                output.id(),
                output.name(),
                output.code(),
                output.icon(),
                output.description(),
                output.isSystem(),
                output.createdBy(),
                foldersWithTemplates,
                output.createdAt()
        );
    }
}

