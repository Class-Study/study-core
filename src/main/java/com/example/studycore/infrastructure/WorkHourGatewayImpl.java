package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.WorkHour;
import com.example.studycore.domain.port.WorkHourGateway;
import com.example.studycore.infrastructure.mapper.WorkHourInfraMapper;
import com.example.studycore.infrastructure.persistence.workhour.WorkHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkHourGatewayImpl implements WorkHourGateway {

    private static final WorkHourInfraMapper MAPPER = WorkHourInfraMapper.INSTANCE;

    private final WorkHourRepository workHourRepository;

    @Override
    public WorkHour save(WorkHour workHour) {
        final var entity = MAPPER.toEntity(workHour);
        final var saved = workHourRepository.save(entity);
        return MAPPER.fromEntity(saved);
    }

    @Override
    public Optional<WorkHour> findByTeacherId(UUID teacherId) {
        return workHourRepository.findByTeacherId(teacherId)
                .map(MAPPER::fromEntity);
    }

    @Override
    public boolean existsByTeacherId(UUID teacherId) {
        return workHourRepository.existsByTeacherId(teacherId);
    }
}

