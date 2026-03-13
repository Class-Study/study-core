package com.example.studycore.infrastructure.persistence;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.infrastructure.mapper.LevelProfileInfraMapper;
import com.example.studycore.infrastructure.persistence.levelprofile.LevelFolderEntity;
import com.example.studycore.infrastructure.persistence.levelprofile.LevelFolderRepository;
import com.example.studycore.infrastructure.persistence.levelprofile.LevelProfileRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LevelProfileGatewayImpl implements LevelProfileGateway {

    private static final LevelProfileInfraMapper LEVEL_PROFILE_INFRA_MAPPER = LevelProfileInfraMapper.INSTANCE;

    private final LevelProfileRepository levelProfileRepository;
    private final LevelFolderRepository levelFolderRepository;

    @Override
    @Transactional
    public LevelProfile save(LevelProfile levelProfile) {
        final var entity = LEVEL_PROFILE_INFRA_MAPPER.toEntity(levelProfile);
        final var saved = levelProfileRepository.save(entity);

        levelFolderRepository.deleteByLevelProfileId(saved.getId());
        final List<LevelFolderEntity> folderEntities = new ArrayList<>();
        if (levelProfile.getFolders() != null) {
            levelProfile.getFolders().forEach(folder -> {
                final var fe = LEVEL_PROFILE_INFRA_MAPPER.toFolderEntity(folder);
                fe.setId(null);
                fe.setLevelProfileId(saved.getId());
                folderEntities.add(fe);
            });
        }
        if (!folderEntities.isEmpty()) {
            levelFolderRepository.saveAll(folderEntities);
        }

        final var folders = levelFolderRepository.findByLevelProfileIdOrderByPositionAsc(saved.getId());
        return LEVEL_PROFILE_INFRA_MAPPER.fromEntity(saved, folders);
    }

    @Override
    public Optional<LevelProfile> findById(UUID id) {
        return levelProfileRepository.findById(id)
                .map(profile -> LEVEL_PROFILE_INFRA_MAPPER.fromEntity(
                        profile,
                        levelFolderRepository.findByLevelProfileIdOrderByPositionAsc(profile.getId())
                ));
    }

    @Override
    public Optional<LevelProfile> findByCode(String code) {
        return levelProfileRepository.findByCodeIgnoreCase(code)
                .map(profile -> LEVEL_PROFILE_INFRA_MAPPER.fromEntity(
                        profile,
                        levelFolderRepository.findByLevelProfileIdOrderByPositionAsc(profile.getId())
                ));
    }

    @Override
    public List<LevelProfile> findAllByCreatedBy(UUID teacherId) {
        return levelProfileRepository.findByCreatedBy(teacherId).stream()
                .map(profile -> LEVEL_PROFILE_INFRA_MAPPER.fromEntity(
                        profile,
                        levelFolderRepository.findByLevelProfileIdOrderByPositionAsc(profile.getId())
                ))
                .toList();
    }

    @Override
    public List<LevelProfile> findSystemProfiles() {
        return levelProfileRepository.findByIsSystemTrue().stream()
                .map(profile -> LEVEL_PROFILE_INFRA_MAPPER.fromEntity(
                        profile,
                        levelFolderRepository.findByLevelProfileIdOrderByPositionAsc(profile.getId())
                ))
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            levelProfileRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Perfil em uso por alunos — remova o vínculo antes de excluir");
        }
    }

    public static List<LevelProfile> mergeAndSort(List<LevelProfile> teacherProfiles, List<LevelProfile> systemProfiles) {
        final var merged = new ArrayList<LevelProfile>();
        merged.addAll(systemProfiles);
        merged.addAll(teacherProfiles);
        merged.sort(Comparator
                .comparing(LevelProfile::isSystem).reversed()
                .thenComparing(LevelProfile::getName, String.CASE_INSENSITIVE_ORDER));
        return merged;
    }
}

