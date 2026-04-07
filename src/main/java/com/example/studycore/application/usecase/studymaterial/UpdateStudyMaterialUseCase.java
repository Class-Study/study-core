package com.example.studycore.application.usecase.studymaterial;

import com.example.studycore.application.mapper.StudyMaterialOutputMapper;
import com.example.studycore.application.usecase.studymaterial.input.UpdateStudyMaterialInput;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateStudyMaterialUseCase {

    private static final StudyMaterialOutputMapper MAPPER = StudyMaterialOutputMapper.INSTANCE;

    private final StudyMaterialGateway studyMaterialGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public StudyMaterialOutput execute(UpdateStudyMaterialInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.updatedBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(input.levelFolderId()))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var existing = studyMaterialGateway.findById(input.materialId())
                .orElseThrow(() -> new NotFoundException("Material de estudo não encontrado."));

        if (!existing.getLevelFolderId().equals(input.levelFolderId())) {
            throw new BusinessException("Material não pertence a esta pasta de nível.");
        }

        final var title = input.title() != null ? input.title() : existing.getTitle();
        final var type = input.type() != null ? input.type() : existing.getType();
        final var url = input.url() != null ? input.url() : existing.getUrl();
        final var convertedHtml = input.convertedHtml() != null ? input.convertedHtml() : existing.getConvertedHtml();
        final var originalFilename = input.originalFilename() != null ? input.originalFilename() : existing.getOriginalFilename();
        final var description = input.description() != null ? input.description() : existing.getDescription();

        final var updated = StudyMaterial.with(
                existing.getId(),
                existing.getLevelFolderId(),
                existing.getSubfolderType(),
                existing.getSubfolderId(),
                title,
                type,
                url,
                convertedHtml,
                originalFilename,
                description,
                existing.getCreatedBy(),
                existing.getCreatedAt(),
                java.time.OffsetDateTime.now()
        );

        final var saved = studyMaterialGateway.save(updated);
        return MAPPER.toOutput(saved);
    }
}

