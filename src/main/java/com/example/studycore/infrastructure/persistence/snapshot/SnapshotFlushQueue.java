package com.example.studycore.infrastructure.persistence.snapshot;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotFlushQueue {

    private final Map<UUID, String> pendingUpdates = new ConcurrentHashMap<>();
    private final Set<UUID> pendingDeletions = ConcurrentHashMap.newKeySet();

    public void enqueue(UUID activityId, String snapshotBase64) {
        pendingUpdates.put(activityId, snapshotBase64);
    }

    public void enqueueForDeletion(UUID activityId) {
        pendingDeletions.add(activityId);
        pendingUpdates.remove(activityId);
    }

    public Map<UUID, String> drainPendingUpdates() {
        Map<UUID, String> result = new ConcurrentHashMap<>(pendingUpdates);
        pendingUpdates.clear();
        return result;
    }

    public Set<UUID> drainPendingDeletions() {
        Set<UUID> result = ConcurrentHashMap.newKeySet();
        result.addAll(pendingDeletions);
        pendingDeletions.clear();
        return result;
    }

    public boolean hasItems() {
        return !pendingUpdates.isEmpty() || !pendingDeletions.isEmpty();
    }
}

