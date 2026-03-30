package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
public class TeacherScheduleConfig {

    private final List<WorkBlock> workBlocks;
    private final Set<DayOfWeek> workDays;
    private final Set<LocalDate> blockedDates;

    private TeacherScheduleConfig(List<WorkBlock> workBlocks, Set<DayOfWeek> workDays, Set<LocalDate> blockedDates) {
        this.workBlocks = workBlocks;
        this.workDays = workDays;
        this.blockedDates = blockedDates;
        validate();
    }

    public static TeacherScheduleConfig with(List<WorkBlock> workBlocks, Set<DayOfWeek> workDays, Set<LocalDate> blockedDates) {
        return new TeacherScheduleConfig(workBlocks, workDays, blockedDates);
    }

    public static TeacherScheduleConfig defaultConfig() {
        return new TeacherScheduleConfig(
                List.of(
                        new WorkBlock(LocalTime.of(6, 0), LocalTime.of(12, 0)),
                        new WorkBlock(LocalTime.of(13, 0), LocalTime.of(18, 0))
                ),
                Set.of(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
                ),
                Set.of()
        );
    }

    private void validate() {
        if (workBlocks == null || workBlocks.isEmpty()) {
            throw new IllegalArgumentException("TeacherScheduleConfig workBlocks cannot be null or empty.");
        }
        if (workDays == null || workDays.isEmpty()) {
            throw new IllegalArgumentException("TeacherScheduleConfig workDays cannot be null or empty.");
        }
        if (blockedDates == null) {
            throw new IllegalArgumentException("TeacherScheduleConfig blockedDates cannot be null.");
        }
        for (WorkBlock block : workBlocks) {
            if (block.start().isAfter(block.end()) || block.start().equals(block.end())) {
                throw new IllegalArgumentException("WorkBlock start must be before end.");
            }
        }
    }

    public record WorkBlock(LocalTime start, LocalTime end) {}
}