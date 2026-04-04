package com.example.studycore.infrastructure.persistence.workhour;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkHourRepository extends JpaRepository<WorkHourEntity, UUID> {

    Optional<WorkHourEntity> findByTeacherId(UUID teacherId);

    boolean existsByTeacherId(UUID teacherId);
}

