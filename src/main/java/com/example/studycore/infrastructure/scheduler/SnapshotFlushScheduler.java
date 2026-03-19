package com.example.studycore.infrastructure.scheduler;

import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import com.example.studycore.infrastructure.persistence.snapshot.SnapshotFlushQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class SnapshotFlushScheduler {

    private final ActivityRepository activityRepository;
    private final SnapshotFlushQueue flushQueue;

    public SnapshotFlushScheduler(
        ActivityRepository activityRepository,
        SnapshotFlushQueue flushQueue
    ) {
        this.activityRepository = activityRepository;
        this.flushQueue = flushQueue;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 30000)  // 30s em ms
    public void flushSnapshotUpdates() {
        if (!flushQueue.hasItems()) {
            return;
        }

        log.debug("[Snapshot Flush] Iniciando flush periódico");

        Map<UUID, String> pendingUpdates = flushQueue.drainPendingUpdates();
        if (!pendingUpdates.isEmpty()) {
            flushUpdatesToDatabase(pendingUpdates);
        }

        Set<UUID> pendingDeletions = flushQueue.drainPendingDeletions();
        if (!pendingDeletions.isEmpty()) {
            flushDeletionsToDatabase(pendingDeletions);
        }

        if (!pendingUpdates.isEmpty() || !pendingDeletions.isEmpty()) {
            log.info("[Snapshot Flush] Flush concluído | updates={} | deletions={}",
                pendingUpdates.size(), pendingDeletions.size());
        }
    }

    private void flushUpdatesToDatabase(Map<UUID, String> pendingUpdates) {
        for (Map.Entry<UUID, String> entry : pendingUpdates.entrySet()) {
            UUID activityId = entry.getKey();
            String snapshotBase64 = entry.getValue();

            try {
                activityRepository.findById(activityId).ifPresentOrElse(
                    entity -> {
                        entity.setSnapshot(snapshotBase64);
                        activityRepository.save(entity);
                        log.debug("[Snapshot Flush] Update persistido | activityId={} | size={}bytes",
                            activityId, snapshotBase64.length());
                    },
                    () -> log.warn("[Snapshot Flush] Atividade não encontrada ao flush | activityId={}", activityId)
                );
            } catch (Exception e) {
                log.error("[Snapshot Flush] Falha ao persistir update | activityId={}", activityId, e);
            }
        }
    }

    private void flushDeletionsToDatabase(Set<UUID> pendingDeletions) {
        for (UUID activityId : pendingDeletions) {
            try {
                activityRepository.findById(activityId).ifPresent(entity -> {
                    entity.setSnapshot(null);
                    activityRepository.save(entity);
                    log.debug("[Snapshot Flush] Snapshot deletado | activityId={}", activityId);
                });
            } catch (Exception e) {
                log.error("[Snapshot Flush] Falha ao deletar snapshot | activityId={}", activityId, e);
            }
        }
    }
}

