package com.example.studycore.infrastructure.persistence.student;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {
    Optional<StudentEntity> findById(UUID id);

    List<StudentEntity> findByTeacherId(UUID teacherId);

    List<StudentEntity> findByLevelProfileId(UUID levelProfileId);

    @Query(value = """
    SELECT DISTINCT s.*
    FROM students s
    CROSS JOIN UNNEST(s.class_days) AS d(day)
    WHERE s.teacher_id = ?1
      AND d.day IN (?2)
      AND s.class_time < CAST(?4 AS time)
      AND (s.class_time + s.class_duration * INTERVAL '1 minute') > CAST(?3 AS time)
      AND s.start_date <= ?6
      AND s.contract_end_date >= ?5
""", nativeQuery = true)
    List<StudentEntity> findRecurringConflictsRaw(
            UUID teacherId,           // ?1
            List<String> days,        // ?2
            LocalTime classTime,      // ?3
            LocalTime classTimeEnd,   // ?4
            LocalDate startDate,      // ?5
            LocalDate contractEndDate // ?6
    );


    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM students s
        WHERE s.teacher_id = :teacherId
          AND :classDay = ANY(s.class_days)
          AND s.class_time < :endClass
          AND (s.class_time + (s.class_duration * INTERVAL '1 minute')) > :startClass
    )
""", nativeQuery = true)
    boolean existsRecurringClassOverlap(
            UUID teacherId,
            String classDay,
            LocalTime startClass,
            LocalTime endClass
    );

    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM students s
        WHERE s.teacher_id = :teacherId
          AND s.class_time < CAST(:endTime AS time)
          AND (s.class_time + (s.class_duration * INTERVAL '1 minute')) > CAST(:startTime AS time)
          AND s.start_date <= CAST(:date AS date)
          AND s.contract_end_date >= CAST(:date AS date)
    )
""", nativeQuery = true)
    boolean existsRecurringOverlapOnDate(
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}


