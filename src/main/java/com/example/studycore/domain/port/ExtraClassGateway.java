package com.example.studycore.domain.port;

import com.example.studycore.domain.model.ExtraClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExtraClassGateway {
    ExtraClass save(ExtraClass extra);

    List<ExtraClass> findByStudentIdsAndStartAtUtcBetween(Collection<UUID> studentIds, OffsetDateTime start, OffsetDateTime end);

    Optional<ExtraClass> findById(UUID id);

    void deleteById(UUID id);

    Optional<ExtraClass> findMostRecentByStudentId(UUID studentId);

    boolean existsByTeacherAndTimeOverlap(
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}
