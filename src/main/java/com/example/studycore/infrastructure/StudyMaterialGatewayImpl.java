package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.model.enums.SubfolderType;
import com.example.studycore.domain.port.StudyMaterialGateway;
import com.example.studycore.infrastructure.mapper.StudyMaterialInfraMapper;
import com.example.studycore.infrastructure.persistence.studymaterial.StudyMaterialRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyMaterialGatewayImpl implements StudyMaterialGateway {

    private static final StudyMaterialInfraMapper MAPPER = StudyMaterialInfraMapper.INSTANCE;

    private final StudyMaterialRepository studyMaterialRepository;

    @Override
    public StudyMaterial save(StudyMaterial studyMaterial) {
        final var saved = studyMaterialRepository.save(MAPPER.toEntity(studyMaterial));
        return MAPPER.fromEntity(saved);
    }

    @Override
    public List<StudyMaterial> findByLevelFolderAndSubfolder(UUID levelFolderId, SubfolderType subfolderType) {
        return studyMaterialRepository
                .findByLevelFolderIdAndSubfolderTypeOrderByCreatedAtAsc(levelFolderId, subfolderType.name())
                .stream()
                .map(MAPPER::fromEntity)
                .toList();
    }

    @Override
    public List<StudyMaterial> findBySubfolderId(UUID subfolderId) {
        return studyMaterialRepository.findBySubfolderIdOrderByCreatedAtAsc(subfolderId)
                .stream().map(MAPPER::fromEntity).toList();
    }

    @Override
    public List<StudyMaterial> findAllByLevelFolderId(UUID levelFolderId) {
        return studyMaterialRepository.findByLevelFolderIdOrderByCreatedAtAsc(levelFolderId)
                .stream()
                .map(MAPPER::fromEntity)
                .toList();
    }

    @Override
    public Optional<StudyMaterial> findById(UUID id) {
        return studyMaterialRepository.findById(id).map(MAPPER::fromEntity);
    }

    @Override
    public void delete(UUID id) {
        studyMaterialRepository.deleteById(id);
    }
}

