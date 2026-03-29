package com.example.studycore.infrastructure.persistence.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExtraClassRepository extends JpaRepository<ExtraClassEntity, UUID> {

    List<ExtraClassEntity> findByStudentIdInAndDateBetweenOrderByDateAsc(Collection<UUID> studentIds, LocalDate start, LocalDate end);

    @Query(value = """
                SELECT *
                FROM extra_classes e
                WHERE e.student_id = ?1
                  AND (
                    e.date > ?2
                    OR (
                      e.date = ?2
                      AND (e.start_time + (e.duration_min * INTERVAL '1 minute')) > ?3
                    )
                  )
                ORDER BY e.date ASC, e.start_time ASC
                LIMIT 1
            """, nativeQuery = true)
    Optional<ExtraClassEntity> findMostRecentByStudentId(
            UUID studentId,
            LocalDate today,
            LocalTime currentTime
    );

    Optional<ExtraClassEntity> findById(UUID id);

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM extra_classes e
                    WHERE e.teacher_id = :teacherId
                      AND e.date = :date
                      AND e.start_time < :endTime
                      AND (e.start_time + (e.duration_min * INTERVAL '1 minute')) > :startTime
                )
            """, nativeQuery = true)
    boolean existsByTeacherAndTimeOverlap(
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    @Query(value = """
                SELECT *
                FROM extra_classes e
                WHERE e.teacher_id = :teacherId
                  AND e.date >= :startDate
                  AND e.date <= :contractEndDate
                  AND EXTRACT(DOW FROM e.date) IN (:dowNumbers)
                  AND e.start_time < CAST(:classTimeEnd AS time)
                  AND (e.start_time + e.duration_min * INTERVAL '1 minute') > CAST(:classTime AS time)
            """, nativeQuery = true)
    List<ExtraClassEntity> findExtraConflictsRaw(
            UUID teacherId,
            List<Integer> dowNumbers,
            LocalTime classTime,
            LocalTime classTimeEnd,
            LocalDate startDate,
            LocalDate contractEndDate
    );
}
