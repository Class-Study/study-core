package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
public class WorkHour {

    private final UUID id;
    private final UUID teacherId;
    private final LocalTime startTimeMorning;
    private final LocalTime endTimeMorning;
    private final LocalTime startTimeAfternoon;
    private final LocalTime endTimeAfternoon;

    private WorkHour(
            UUID id,
            UUID teacherId,
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ) {
        this.id = id;
        this.teacherId = teacherId;
        this.startTimeMorning = startTimeMorning;
        this.endTimeMorning = endTimeMorning;
        this.startTimeAfternoon = startTimeAfternoon;
        this.endTimeAfternoon = endTimeAfternoon;
    }

    public static WorkHour create(
            UUID teacherId,
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ) {
        return new WorkHour(
                UUID.randomUUID(),
                teacherId,
                startTimeMorning,
                endTimeMorning,
                startTimeAfternoon,
                endTimeAfternoon
        );
    }

    public static WorkHour createDefault(UUID teacherId) {
        return new WorkHour(
                UUID.randomUUID(),
                teacherId,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(18, 0)
        );
    }

    public WorkHour update(
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ) {
        return new WorkHour(
                this.id,
                this.teacherId,
                startTimeMorning != null ? startTimeMorning : this.startTimeMorning,
                endTimeMorning != null ? endTimeMorning : this.endTimeMorning,
                startTimeAfternoon != null ? startTimeAfternoon : this.startTimeAfternoon,
                endTimeAfternoon != null ? endTimeAfternoon : this.endTimeAfternoon
        );
    }

    public static WorkHour with(
            UUID id,
            UUID teacherId,
            LocalTime startTimeMorning,
            LocalTime endTimeMorning,
            LocalTime startTimeAfternoon,
            LocalTime endTimeAfternoon
    ) {
        return new WorkHour(
                id,
                teacherId,
                startTimeMorning,
                endTimeMorning,
                startTimeAfternoon,
                endTimeAfternoon
        );
    }
}
