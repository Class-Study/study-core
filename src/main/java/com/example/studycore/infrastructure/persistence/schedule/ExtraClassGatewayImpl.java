package com.example.studycore.infrastructure.persistence.schedule;

import com.example.studycore.domain.model.ExtraClass;
import com.example.studycore.domain.port.ExtraClassGateway;
import com.example.studycore.infrastructure.mapper.SchedulerInfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExtraClassGatewayImpl implements ExtraClassGateway {

    private final ExtraClassRepository repository;

    private final static SchedulerInfraMapper SCHEDULER_INFRA_MAPPER = SchedulerInfraMapper.INSTANCE;

    public ExtraClass save(ExtraClass extra) {
        var entity = SCHEDULER_INFRA_MAPPER.toEntity(extra);
        var saved = repository.save(entity);
        return SCHEDULER_INFRA_MAPPER.fromEntity(saved);

    }
    public List<ExtraClass> findByStartWeekBetweenEndWeek(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) return Collections.emptyList();

        // Converter intervalo UTC para intervalo de LocalDate com base nas datas (inclusive)
        final LocalDate startDate = start.toLocalDate();
        final LocalDate endDate = end.toLocalDate();

        return repository.findByDateBetweenOrderByDateAsc(startDate, endDate)
                .stream().map(SCHEDULER_INFRA_MAPPER::fromEntity).toList();
    }

    public List<ExtraClass> findByStudentIdsAndStartAtUtcBetween(java.util.Collection<java.util.UUID> studentIds, OffsetDateTime start, OffsetDateTime end) {
        if (studentIds == null || studentIds.isEmpty() || start == null || end == null)
            return Collections.emptyList();

        final LocalDate startDate = start.toLocalDate();
        final LocalDate endDate = end.toLocalDate();

        return repository.findByStudentIdInAndDateBetweenOrderByDateAsc(studentIds, startDate, endDate)
                .stream().map(SCHEDULER_INFRA_MAPPER::fromEntity).toList();
    }

    public Optional<ExtraClass> findById(UUID id) {
        return repository.findById(id).map(SCHEDULER_INFRA_MAPPER::fromEntity);
    }
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}

