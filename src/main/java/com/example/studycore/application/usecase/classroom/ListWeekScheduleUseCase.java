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

            if (classDays == null || classDays.isEmpty() || classTime == null) continue;
        }

        final OffsetDateTime start = weekStart.atStartOfDay().atOffset(ZoneOffset.UTC);
        final OffsetDateTime end = weekEnd.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        final Collection<UUID> studentIds = students.stream().map(Student::getId).toList();
        final List<Classroom> classrooms = classroomGateway.findByStudentIdsAndStartAtUtcBetween(studentIds, start, end);
        for (Classroom classroom : classrooms) {
            Student s = studentsById.get(classroom.getStudentId());
            if (s == null) {
                s = studentGateway.findById(classroom.getStudentId()).orElse(null);
            }

            events.add(new ScheduleEventOutput(
                    classroom.getId(),
                    classroom.getStudentId(),
                    s.getName(),
                    classroom.getDate(),
                    classroom.getStartTime(),
                    classroom.getDurationMin(),
                    classroom.getType() != null ? classroom.getType().name() : null,
                    classroom.getTitle(),
                    s.getMeetLink(),
                    s.getMeetPlatform(),
                    s.getStatus() != null ? s.getStatus().name() : null,
                    s.getLevelProfileId() != null ? s.getLevelProfileId().toString() : null
            ));
        }

        // sort by date then startTime
        events.sort(Comparator.comparing(ScheduleEventOutput::date).thenComparing(ScheduleEventOutput::startTime));

        return new ScheduleWeekOutput(weekStart, weekEnd, events);
    }
}
