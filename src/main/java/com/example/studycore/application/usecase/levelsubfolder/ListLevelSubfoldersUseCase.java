package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.application.usecase.levelsubfolder.output.LevelSubfolderOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.LevelSubfolder;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListLevelSubfoldersUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;

    public List<LevelSubfolderOutput> execute(UUID levelProfileId, UUID levelFolderId, UUID requestedBy) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !requestedBy.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        return levelSubfolderGateway.findByLevelFolderId(levelFolderId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    private LevelSubfolderOutput toOutput(LevelSubfolder s) {
        return new LevelSubfolderOutput(s.getId(), s.getLevelFolderId(), s.getName(),
                s.getPosition(), s.getCreatedBy(), s.getCreatedAt(), s.getUpdatedAt());
    }
}

