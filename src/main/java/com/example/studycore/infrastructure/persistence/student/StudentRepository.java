package com.example.studycore.infrastructure.persistence.student;

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
}
