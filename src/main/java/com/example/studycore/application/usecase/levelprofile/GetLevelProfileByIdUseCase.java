package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLevelProfileByIdUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;

    public GetLevelProfileOutput execute(UUID id, UUID teacherId) {
        final var profile = levelProfileGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!profile.isSystem() && !teacherId.equals(profile.getCreatedBy())) {
            throw new BusinessException("Sem acesso a este perfil de nível.");
        }

        return MAPPER.toGetLevelProfileOutput(profile);
    }
}

