package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.port.ClassroomGateway;
import com.example.studycore.infrastructure.mapper.SchedulerInfraMapper;
import com.example.studycore.infrastructure.persistence.classroom.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ClassroomGatewayImpl implements ClassroomGateway {

    private final ClassroomRepository repository;

    private final static SchedulerInfraMapper SCHEDULER_INFRA_MAPPER = SchedulerInfraMapper.INSTANCE;

    private static final Map<String, Integer> DAY_TO_DOW = Map.of(
            "SUNDAY",    0,
            "MONDAY",    1,
            "TUESDAY",   2,
            "WEDNESDAY", 3,
            "THURSDAY",  4,
            "FRIDAY",    5,
            "SATURDAY",  6
    );

    public Classroom save(Classroom extra) {
        var entity = SCHEDULER_INFRA_MAPPER.toEntity(extra);
        var saved = repository.save(entity);
        return SCHEDULER_INFRA_MAPPER.fromEntity(saved);

    }

    public List<Classroom> findByStudentIdsAndStartAtUtcBetween(java.util.Collection<java.util.UUID> studentIds, OffsetDateTime start, OffsetDateTime end) {
        if (studentIds == null || studentIds.isEmpty() || start == null || end == null)
            return Collections.emptyList();

        final LocalDate startDate = start.toLocalDate();
        final LocalDate endDate = end.toLocalDate();

        return repository.findByStudentIdInAndDateBetweenOrderByDateAsc(studentIds, startDate, endDate)
                .stream().map(SCHEDULER_INFRA_MAPPER::fromEntity).toList();
    }

    public Optional<Classroom> findById(UUID id) {
        return repository.findById(id).map(SCHEDULER_INFRA_MAPPER::fromEntity);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Classroom> findMostRecentByStudentId(UUID studentId) {
        return repository
                .findMostRecentByStudentId(
                        studentId,
                        LocalDate.now(),
                        LocalTime.now()
                )
                .map(SCHEDULER_INFRA_MAPPER::fromEntity);
    }

    @Override
    public boolean existsByTeacherAndTimeOverlap(
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return repository.existsByTeacherAndTimeOverlap(teacherId, date, startTime, endTime);
    }


    @Override
    public List<Classroom> findExtraConflicts(
            UUID teacherId,
            List<String> days,
            LocalTime classTime,
            int durationMin,
            LocalDate startDate,
            LocalDate contractEndDate
    ) {
        final var classTimeEnd = classTime.plusMinutes(durationMin);
        final var dowNumbers = days.stream().map(DAY_TO_DOW::get).toList();
        return repository
                .findExtraConflictsRaw(teacherId, dowNumbers, classTime, classTimeEnd, startDate, contractEndDate)
                .stream().map(SCHEDULER_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    public List<Classroom> findByStudentIdAndDateBetween(UUID studentId, LocalDate from, LocalDate to) {
        return repository.findByStudentIdAndDateBetweenOrderByDateAsc(studentId, from, to)
                .stream().map(SCHEDULER_INFRA_MAPPER::fromEntity).toList();
    }
}
