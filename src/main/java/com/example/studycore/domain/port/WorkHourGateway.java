package com.example.studycore.domain.port;

import com.example.studycore.domain.model.WorkHour;

import java.util.Optional;
import java.util.UUID;

public interface WorkHourGateway {

    WorkHour save(WorkHour workHour);

    Optional<WorkHour> findByTeacherId(UUID teacherId);

    boolean existsByTeacherId(UUID teacherId);
}

