package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.classroom.input.CreateClassroomInput;
import com.example.studycore.application.usecase.classroom.input.ScheduleWeekInput;
import com.example.studycore.application.usecase.classroom.output.RescheduleOptionInput;
import com.example.studycore.application.usecase.classroom.output.RescheduleOptionOutput;
import com.example.studycore.application.usecase.classroom.output.ScheduleWeekOutput;
import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.model.enums.ScheduleType;
import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateClassroomRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.RescheduleOptionResponse;
import com.example.studycore.infrastructure.persistence.classroom.ClassroomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

@Mapper
public interface SchedulerInfraMapper {

    SchedulerInfraMapper INSTANCE = Mappers.getMapper(SchedulerInfraMapper.class);


    default Classroom fromEntity(ClassroomEntity entity) {
        if (entity == null) {
            return null;
        }
        return Classroom.with(
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

    default ClassroomEntity toEntity(Classroom domain) {
        if (domain == null) return null;
        final var entity = new ClassroomEntity();
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

    @Mapping(target = "scheduleType", expression = "java(ScheduleType.valueOf(scheduleType))")
    RescheduleOptionInput toRescheduleOptionInput(UUID teacherId, UUID schedulerId, String scheduleType, LocalDate date);


    ScheduleWeekResponse toResponse(ScheduleWeekOutput out);

    RescheduleOptionResponse toRescheduleOptionResponse(RescheduleOptionOutput output);

    @Mapping(target = "type", expression = "java(ScheduleType.valueOf(createClassroomRequest.type()))")
    CreateClassroomInput toCreateClassroomInput(CreateClassroomRequest createClassroomRequest);

}




