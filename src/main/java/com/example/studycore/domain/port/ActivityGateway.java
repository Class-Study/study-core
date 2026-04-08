package com.example.studycore.domain.port;

import com.example.studycore.domain.model.Activity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityGateway {
    Activity save(Activity activity);
    Optional<Activity> findById(UUID id);
    List<Activity> findByFolderId(UUID folderId);
    List<Activity> findBySubfolderId(UUID subfolderId);
    void delete(UUID id);
}
