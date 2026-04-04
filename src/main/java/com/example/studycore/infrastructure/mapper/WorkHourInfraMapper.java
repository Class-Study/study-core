package com.example.studycore.infrastructure.mapper;

import com.example.studycore.domain.model.WorkHour;
import com.example.studycore.infrastructure.persistence.workhour.WorkHourEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkHourInfraMapper {

    WorkHourInfraMapper INSTANCE = Mappers.getMapper(WorkHourInfraMapper.class);

    // Entity → Domain
    default WorkHour fromEntity(WorkHourEntity entity) {
        if (entity == null) return null;
        return WorkHour.with(
                entity.getId(),
                entity.getTeacherId(),
                entity.getStartTimeMorning(),
                entity.getEndTimeMorning(),
                entity.getStartTimeAfternoon(),
                entity.getEndTimeAfternoon()
        );
    }

    // Domain → Entity
    default WorkHourEntity toEntity(WorkHour workHour) {
        if (workHour == null) return null;
        final WorkHourEntity entity = new WorkHourEntity();
        entity.setId(workHour.getId());
        entity.setTeacherId(workHour.getTeacherId());
        entity.setStartTimeMorning(workHour.getStartTimeMorning());
        entity.setEndTimeMorning(workHour.getEndTimeMorning());
        entity.setStartTimeAfternoon(workHour.getStartTimeAfternoon());
        entity.setEndTimeAfternoon(workHour.getEndTimeAfternoon());
        return entity;
    }
}

