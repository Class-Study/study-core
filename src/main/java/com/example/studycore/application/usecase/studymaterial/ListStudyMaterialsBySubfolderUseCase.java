package com.example.studycore.application.usecase.studymaterial;

import com.example.studycore.application.mapper.StudyMaterialOutputMapper;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListStudyMaterialsBySubfolderUseCase {

    private static final StudyMaterialOutputMapper MAPPER = StudyMaterialOutputMapper.INSTANCE;

    private final StudyMaterialGateway studyMaterialGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final LevelSubfolderGateway levelSubfolderGateway;

    public List<StudyMaterialOutput> execute(UUID levelProfileId, UUID levelFolderId, UUID subfolderId, UUID requestedBy) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !requestedBy.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        levelSubfolderGateway.findByIdAndLevelFolderId(subfolderId, levelFolderId)
                .orElseThrow(() -> new NotFoundException("Subpasta não encontrada nesta pasta de nível."));

        return studyMaterialGateway.findBySubfolderId(subfolderId)
                .stream()
                .map(MAPPER::toOutput)
                .toList();
    }
}
