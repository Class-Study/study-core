package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.infrastructure.mapper.FolderInfraMapper;
import com.example.studycore.infrastructure.persistence.folder.FolderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FolderGatewayImpl implements FolderGateway {

    private static final FolderInfraMapper FOLDER_INFRA_MAPPER = FolderInfraMapper.INSTANCE;

    private final FolderRepository folderRepository;

    @Override
    public Folder save(Folder folder) {
        final var saved = folderRepository.save(FOLDER_INFRA_MAPPER.toEntity(folder));
        return FOLDER_INFRA_MAPPER.fromEntity(saved);
    }

    @Override
    public List<Folder> saveAll(List<Folder> folders) {
        final var entities = folders.stream().map(FOLDER_INFRA_MAPPER::toEntity).toList();
        return folderRepository.saveAll(entities).stream().map(FOLDER_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    public Optional<Folder> findById(UUID id) {
        return folderRepository.findById(id).map(FOLDER_INFRA_MAPPER::fromEntity);
    }

    @Override
    public List<Folder> findByStudentId(UUID studentId) {
        return folderRepository.findByStudentIdOrderByPositionAsc(studentId).stream()
                .map(FOLDER_INFRA_MAPPER::fromEntity)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        folderRepository.deleteById(id);
    }
}

