package com.example.studycore.domain.port;

import com.example.studycore.domain.model.Classroom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassroomGateway {
    Classroom save(Classroom extra);

    List<Classroom> findByStudentIdsAndStartAtUtcBetween(Collection<UUID> studentIds, OffsetDateTime start, OffsetDateTime end);

    Optional<Classroom> findById(UUID id);

    void deleteById(UUID id);

    Optional<Classroom> findMostRecentByStudentId(UUID studentId);

    boolean existsByTeacherAndTimeOverlap(
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    List<Classroom> findExtraConflicts(
            UUID teacherId,
            List<String> days,
            LocalTime classTime,
            int durationMin,
            LocalDate startDate,
            LocalDate contractEndDate
    );
}
