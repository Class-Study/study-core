package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.infrastructure.mapper.ActivityInfraMapper;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityGatewayImpl implements ActivityGateway {

    private static final ActivityInfraMapper ACTIVITY_INFRA_MAPPER = ActivityInfraMapper.INSTANCE;

    private final ActivityRepository activityRepository;

    @Override
    public Activity save(Activity activity) {
        final var saved = activityRepository.save(ACTIVITY_INFRA_MAPPER.toEntity(activity));
        return ACTIVITY_INFRA_MAPPER.fromEntity(saved);
    }

    @Override
    public Optional<Activity> findById(UUID id) {
        return activityRepository.findById(id).map(ACTIVITY_INFRA_MAPPER::fromEntity);
    }

    @Override
    public List<Activity> findByFolderId(UUID folderId) {
        return activityRepository.findByFolderIdOrderByCreatedAtAsc(folderId).stream()
                .map(ACTIVITY_INFRA_MAPPER::fromEntity)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        activityRepository.deleteById(id);
    }
}

