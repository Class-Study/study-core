package com.example.studycore.infrastructure.persistence.snapshot;

import com.example.studycore.domain.port.out.SnapshotPersistencePort;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SnapshotPersistenceAdapter implements SnapshotPersistencePort {

    private static final String REDIS_SNAPSHOT_KEY_PREFIX = "snapshot:";
    private static final long REDIS_TTL_HOURS = 24;

    private final RedisTemplate<String, String> redisTemplate;
    private final ActivityRepository activityRepository;
    private final SnapshotFlushQueue flushQueue;

    public SnapshotPersistenceAdapter(
        RedisTemplate<String, String> redisTemplate,
        ActivityRepository activityRepository,
        SnapshotFlushQueue flushQueue
    ) {
        this.redisTemplate = redisTemplate;
        this.activityRepository = activityRepository;
        this.flushQueue = flushQueue;
    }

    @Override
    public void saveSnapshot(UUID activityId, String snapshotBase64) {
        if (activityId == null || snapshotBase64 == null) {
            log.warn("[Snapshot] saveSnapshot recebido com parâmetros nulos");
            return;
        }

        String redisKey = REDIS_SNAPSHOT_KEY_PREFIX + activityId;

        try {
            // Salvar em Redis imediatamente (quente)
            redisTemplate.opsForValue().set(
                redisKey,
                snapshotBase64,
                REDIS_TTL_HOURS,
                TimeUnit.HOURS
            );
            log.debug("[Snapshot] Estado salvo em Redis | activityId={} | size={}bytes",
                activityId, snapshotBase64.length());

            // Enfileirar para flush periodico em PostgreSQL
            flushQueue.addUpdate(activityId, snapshotBase64);

        } catch (Exception e) {
            log.error("[Snapshot] Falha ao salvar em Redis, tentando PostgreSQL | activityId={}", activityId, e);
            // Fallback: salvar direto em PostgreSQL
            saveSnapshotToDB(activityId, snapshotBase64);
        }
    }

    @Override
    public Optional<String> getSnapshot(UUID activityId) {
        if (activityId == null) {
            return Optional.empty();
        }

        String redisKey = REDIS_SNAPSHOT_KEY_PREFIX + activityId;

        try {
            // Tentar Redis primeiro (quente)
            String cached = redisTemplate.opsForValue().get(redisKey);
            if (cached != null) {
                log.debug("[Snapshot] Estado recuperado do Redis | activityId={}", activityId);
                return Optional.of(cached);
            }
        } catch (Exception e) {
            log.warn("[Snapshot] Falha ao consultar Redis, tentando PostgreSQL | activityId={}", activityId, e);
        }

        // Fallback: recuperar de PostgreSQL (frio)
        try {
            return activityRepository.findById(activityId)
                .flatMap(entity -> {
                    if (entity.getSnapshot() != null) {
                        String base64 = entity.getSnapshot();
                        log.debug("[Snapshot] Estado recuperado do PostgreSQL | activityId={}", activityId);
                        // Repopular Redis para próxima consulta
                        try {
                            redisTemplate.opsForValue().set(
                                redisKey,
                                base64,
                                REDIS_TTL_HOURS,
                                TimeUnit.HOURS
                            );
                        } catch (Exception e) {
                            log.warn("[Snapshot] Falha ao repopular Redis | activityId={}", activityId, e);
                        }
                        return Optional.of(base64);
                    }
                    return Optional.empty();
                });
        } catch (Exception e) {
            log.error("[Snapshot] Falha ao recuperar estado | activityId={}", activityId, e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteSnapshot(UUID activityId) {
        if (activityId == null) {
            return;
        }

        String redisKey = REDIS_SNAPSHOT_KEY_PREFIX + activityId;

        try {
            // Deletar do Redis
            redisTemplate.delete(redisKey);
            log.debug("[Snapshot] Snapshot deletado do Redis | activityId={}", activityId);
        } catch (Exception e) {
            log.warn("[Snapshot] Falha ao deletar do Redis | activityId={}", activityId, e);
        }

        // Enfileirar para delecao em PostgreSQL
        flushQueue.markForDeletion(activityId);
    }

    /**
     * Salva snapshot diretamente em PostgreSQL (fallback).
     */
    private void saveSnapshotToDB(UUID activityId, String snapshotBase64) {
        try {
            activityRepository.findById(activityId).ifPresentOrElse(
                entity -> {
                    entity.setSnapshot(snapshotBase64);
                    activityRepository.save(entity);
                    log.info("[Snapshot] Estado salvo em PostgreSQL (fallback) | activityId={}", activityId);
                },
                () -> log.warn("[Snapshot] Atividade não encontrada | activityId={}", activityId)
            );
        } catch (Exception e) {
            log.error("[Snapshot] Falha crítica ao salvar em PostgreSQL | activityId={}", activityId, e);
        }
    }
}

