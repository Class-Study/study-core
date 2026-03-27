package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.schedule.input.CreateExtraClassInput;
import com.example.studycore.application.usecase.schedule.input.ScheduleWeekInput;
import com.example.studycore.application.usecase.schedule.output.ScheduleWeekOutput;
import com.example.studycore.domain.model.ExtraClass;
import com.example.studycore.domain.model.enums.ScheduleType;
import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateExtraClassRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import com.example.studycore.infrastructure.persistence.schedule.ExtraClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

@Mapper
public interface SchedulerInfraMapper {

    SchedulerInfraMapper INSTANCE = Mappers.getMapper(SchedulerInfraMapper.class);


    default ExtraClass fromEntity(ExtraClassEntity entity) {
        if (entity == null) {
            return null;
        }
        return ExtraClass.with(
                entity.getId(),
                entity.getStudentId(),
                entity.getTeacherId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getDurationMin(),
                entity.getTitle(),
                ScheduleType.valueOf(entity.getType()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default ExtraClassEntity toEntity(ExtraClass domain) {
        if (domain == null) return null;
        final var entity = new ExtraClassEntity();
        entity.setId(domain.getId());
        entity.setStudentId(domain.getStudentId());
        entity.setTeacherId(domain.getTeacherId());
        entity.setType(domain.getType().name());
        entity.setDate(domain.getDate());
        entity.setStartTime(domain.getStartTime());
        entity.setDurationMin(domain.getDurationMin());
        entity.setTitle(domain.getTitle());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    ScheduleWeekInput toScheduleWeekInput(final LocalDate weekStart, final LocalDate weekEnd, final UUID teacherId);

    ScheduleWeekResponse toResponse(ScheduleWeekOutput out);

    @Mapping(target = "type", expression = "java(ScheduleType.valueOf(createExtraClassRequest.type()))")
    CreateExtraClassInput toCreateExtraClassInput(CreateExtraClassRequest createExtraClassRequest);
}




