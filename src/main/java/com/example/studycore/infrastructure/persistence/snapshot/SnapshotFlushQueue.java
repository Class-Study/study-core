package com.example.studycore.infrastructure.persistence.snapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnapshotFlushQueue {

    private final Map<UUID, String> pendingUpdates = new HashMap<>();
    private final Set<UUID> pendingDeletions = new HashSet<>();
    private final Object queueLock = new Object();

    public void addUpdate(UUID activityId, String snapshotBase64) {
        if (activityId == null || snapshotBase64 == null) {
            log.warn("[SnapshotFlushQueue] Tentativa de adicionar snapshot invalido");
            return;
        }

        synchronized (queueLock) {
            pendingUpdates.put(activityId, snapshotBase64);
            pendingDeletions.remove(activityId);
        }

        log.debug("[SnapshotFlushQueue] Adicionando snapshot a fila | activityId={} | size={}bytes",
            activityId, snapshotBase64.length());
    }

    public void markForDeletion(UUID activityId) {
        if (activityId == null) {
            return;
        }

        synchronized (queueLock) {
            pendingDeletions.add(activityId);
            pendingUpdates.remove(activityId);
        }

        log.debug("[SnapshotFlushQueue] Marcando para delecao | activityId={}", activityId);
    }

    // Backward-compatible alias used by existing call sites.
    public void enqueue(UUID activityId, String snapshotBase64) {
        addUpdate(activityId, snapshotBase64);
    }

    // Backward-compatible alias used by existing call sites.
    public void enqueueForDeletion(UUID activityId) {
        markForDeletion(activityId);
    }

    public Map<UUID, String> drainPendingUpdates() {
        synchronized (queueLock) {
            Map<UUID, String> updates = new HashMap<>(pendingUpdates);
            pendingUpdates.clear();
            return updates;
        }
    }

    public Set<UUID> drainPendingDeletions() {
        synchronized (queueLock) {
            Set<UUID> deletions = new HashSet<>(pendingDeletions);
            pendingDeletions.clear();
            return deletions;
        }
    }

    public boolean hasItems() {
        synchronized (queueLock) {
            return !pendingUpdates.isEmpty() || !pendingDeletions.isEmpty();
        }
    }
}
