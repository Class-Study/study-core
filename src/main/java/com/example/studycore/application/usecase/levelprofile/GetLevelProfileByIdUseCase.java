package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.mapper.StudyMaterialOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderTemplateOutput;
import com.example.studycore.application.usecase.levelprofile.output.SubfolderOutput;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLevelProfileByIdUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;
    private static final StudyMaterialOutputMapper STUDY_MATERIAL_MAPPER = StudyMaterialOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final StudyMaterialGateway studyMaterialGateway;
    private final LevelSubfolderGateway levelSubfolderGateway;

    public GetLevelProfileOutput execute(UUID id, UUID teacherId) {
        final var profile = levelProfileGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!profile.isSystem() && !teacherId.equals(profile.getCreatedBy())) {
            throw new BusinessException("Sem acesso a este perfil de nível.");
        }

        final var output = MAPPER.toGetLevelProfileOutput(profile);

        final var foldersWithSubfolders = output.folders().stream()
                .map(folder -> {
                    // Load all legacy templates (subfolder_id = null) for backward compat
                    final var legacyTemplates = levelFolderTemplateGateway.findAllByFolderId(folder.id()).stream()
                            .filter(t -> t.getSubfolderId() == null)
                            .map(t -> new LevelFolderTemplateOutput(
                                    t.getId(), t.getLevelFolderId(), t.getTitle(),
                                    t.getType(), t.getOriginalFilename(), t.getConvertedHtml(), t.getCreatedAt()))
                            .toList();

                    // Load real subfolders
                    final var subfolders = levelSubfolderGateway.findByLevelFolderId(folder.id())
                            .stream()
                            .map(subfolder -> {
                                final var templates = levelFolderTemplateGateway
                                        .findBySubfolderId(subfolder.getId())
                                        .stream()
                                        .map(t -> new LevelFolderTemplateOutput(
                                                t.getId(), t.getLevelFolderId(), t.getTitle(),
                                                t.getType(), t.getOriginalFilename(), t.getConvertedHtml(), t.getCreatedAt()))
                                        .toList();

                                final var materials = studyMaterialGateway
                                        .findBySubfolderId(subfolder.getId())
                                        .stream()
                                        .map(STUDY_MATERIAL_MAPPER::toOutput)
                                        .toList();

                                return new SubfolderOutput(
                                        subfolder.getId(),
                                        subfolder.getName(),
                                        subfolder.getPosition(),
                                        templates,
                                        materials,
                                        subfolder.getCreatedAt(),
                                        subfolder.getUpdatedAt()
                                );
                            })
                            .toList();

                    return new LevelFolderOutput(
                            folder.id(),
                            folder.name(),
                            folder.position(),
                            legacyTemplates.size(),
                            legacyTemplates,
                            subfolders
                    );
                })
                .toList();

        return new GetLevelProfileOutput(
                output.id(),
                output.name(),
                output.code(),
                output.icon(),
                output.description(),
                output.isSystem(),
                output.createdBy(),
                foldersWithSubfolders,
                output.createdAt()
        );
    }
}
