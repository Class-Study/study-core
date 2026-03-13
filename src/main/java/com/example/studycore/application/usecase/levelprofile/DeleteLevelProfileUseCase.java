package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteLevelProfileUseCase {

    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public void execute(UUID id, UUID teacherId) {
        final var existing = levelProfileGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (existing.isSystem()) {
            throw new BusinessException("Perfis de sistema não podem ser excluídos");
        }
        if (!teacherId.equals(existing.getCreatedBy())) {
            throw new BusinessException("Sem acesso a este perfil de nível.");
        }

        levelProfileGateway.delete(id);
    }
}

