package com.example.studycore.domain.model;

import com.example.studycore.domain.model.enums.ScheduleType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class ExtraClass {

    private final UUID id;
    private final UUID studentId;
    private final UUID teacherId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final Integer durationMin;
    private final String title;
    private final ScheduleType type;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private ExtraClass(
            UUID id,
            UUID studentId,
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            Integer durationMin,
            String title,
            ScheduleType type,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.date = date;
        this.startTime = startTime;
        this.durationMin = durationMin;
        this.title = title;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validate();
    }

    public static ExtraClass create(
            UUID studentId,
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            Integer durationMin,
            String title,
            ScheduleType type
    ) {
        final UUID id = UUID.randomUUID();
        final var now = OffsetDateTime.now();

        return new ExtraClass(id, studentId, teacherId, date, startTime, durationMin, title, type, now, now);
    }

    public static ExtraClass with(
            UUID id,
            UUID studentId,
            UUID teacherId,
            LocalDate date,
            LocalTime startTime,
            Integer durationMin,
            String title,
            ScheduleType type,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new ExtraClass(id, studentId, teacherId, date, startTime, durationMin, title, type, createdAt, updatedAt);
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("id não pode ser nulo");
        if (studentId == null) throw new IllegalArgumentException("studentId não pode ser nulo");
        if (teacherId == null) throw new IllegalArgumentException("teacherId não pode ser nulo");
        if (date == null) throw new IllegalArgumentException("date não pode ser nulo");
        if (startTime == null) throw new IllegalArgumentException("startTime não pode ser nulo");
        if (durationMin == null || durationMin <= 0)
            throw new IllegalArgumentException("durationMin deve ser positivo");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title não pode ser nulo ou em branco");
    }
}

