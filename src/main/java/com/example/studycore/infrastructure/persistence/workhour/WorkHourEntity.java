package com.example.studycore.infrastructure.persistence.workhour;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "work_hours")
public class WorkHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "teacher_id", nullable = false, unique = true)
    private UUID teacherId;

    @Column(name = "start_time_morning", nullable = false)
    private LocalTime startTimeMorning;

    @Column(name = "end_time_morning", nullable = false)
    private LocalTime endTimeMorning;

    @Column(name = "start_time_afternoon", nullable = false)
    private LocalTime startTimeAfternoon;

    @Column(name = "end_time_afternoon", nullable = false)
    private LocalTime endTimeAfternoon;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}

