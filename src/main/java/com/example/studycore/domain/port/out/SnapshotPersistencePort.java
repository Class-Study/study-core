package com.example.studycore.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface SnapshotPersistencePort {

    void saveSnapshot(UUID activityId, String snapshotBase64);

    Optional<String> getSnapshot(UUID activityId);

    void deleteSnapshot(UUID activityId);
}

