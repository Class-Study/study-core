package com.example.studycore.domain.port;

import com.example.studycore.domain.model.LevelProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LevelProfileGateway {
    LevelProfile save(LevelProfile levelProfile);
    Optional<LevelProfile> findById(UUID id);
    Optional<LevelProfile> findByCode(String code);
    List<LevelProfile> findAllByCreatedBy(UUID teacherId);
    List<LevelProfile> findSystemProfiles();
    void delete(UUID id);
}

