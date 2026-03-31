package com.example.studycore.application.usecase.classroom;

import com.example.studycore.application.usecase.classroom.input.ScheduleWeekInput;
import com.example.studycore.application.usecase.classroom.output.ScheduleEventOutput;
import com.example.studycore.application.usecase.classroom.output.ScheduleWeekOutput;
import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.ScheduleType;
import com.example.studycore.domain.port.ClassroomGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListWeekScheduleUseCase {

    private final ClassroomGateway classroomGateway;
    private final StudentGateway studentGateway;

    public ScheduleWeekOutput execute(ScheduleWeekInput input) {
        if (input == null) throw new IllegalArgumentException("input cannot be null");

        final LocalDate weekStart = input.weekStart();
        final LocalDate weekEnd = input.weekEnd();

        // fetch students for the teacher
        final List<Student> students = studentGateway.findAllByTeacherId(input.teacherId());
        final Map<UUID, Student> studentsById = students.stream()
                .collect(Collectors.toMap(Student::getId, s -> s));

        final List<ScheduleEventOutput> events = new ArrayList<>();

        // expand recurring classes from student schedule
        for (Student s : students) {
            final List<String> classDays = s.getClassDays();
            final var classTime = s.getClassTime();
            final var duration = s.getClassDuration();

            if (classDays == null || classDays.isEmpty() || classTime == null) continue;

            for (String dayStr : classDays) {
                if (dayStr == null) continue;
                try {
                    final DayOfWeek dow = DayOfWeek.valueOf(dayStr.toUpperCase());
                    final LocalDate occurrence = weekStart.with(TemporalAdjusters.nextOrSame(dow));
                    if (occurrence.isAfter(weekEnd)) continue;

                    events.add(new ScheduleEventOutput(
                            s.getId().toString(),
                            s.getId(),
                            s.getName(),
                            occurrence,
                            classTime,
                            duration,
                            ScheduleType.RECURRING.name(),
                            null,
                            s.getMeetLink(),
                            s.getMeetPlatform(),
                            s.getStatus() != null ? s.getStatus().name() : null,
                            s.getLevelProfileId() != null ? s.getLevelProfileId().toString() : null
                    ));
                } catch (Exception ex) {
                    // ignore invalid day strings
                }
            }
        }

        final OffsetDateTime start = weekStart.atStartOfDay().atOffset(ZoneOffset.UTC);
        final OffsetDateTime end = weekEnd.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        final Collection<UUID> studentIds = students.stream().map(Student::getId).toList();
        final List<Classroom> extras = classroomGateway.findByStudentIdsAndStartAtUtcBetween(studentIds, start, end);
        for (Classroom extra : extras) {
            Student s = studentsById.get(extra.getStudentId());
            if (s == null) {
                s = studentGateway.findById(extra.getStudentId()).orElse(null);
            }

            events.add(new ScheduleEventOutput(
                    extra.getId().toString(),
                    extra.getStudentId(),
                    s != null ? s.getName() : null,
                    extra.getDate(),
                    extra.getStartTime(),
                    extra.getDurationMin(),
                    extra.getType() != null ? extra.getType().name() : null,
                    extra.getTitle(),
                    s != null ? s.getMeetLink() : null,
                    s != null ? s.getMeetPlatform() : null,
                    s != null && s.getStatus() != null ? s.getStatus().name() : null,
                    s != null && s.getLevelProfileId() != null ? s.getLevelProfileId().toString() : null
            ));
        }

        // sort by date then startTime
        events.sort(Comparator.comparing(ScheduleEventOutput::date).thenComparing(ScheduleEventOutput::startTime));

        return new ScheduleWeekOutput(weekStart, weekEnd, events);
    }
}
