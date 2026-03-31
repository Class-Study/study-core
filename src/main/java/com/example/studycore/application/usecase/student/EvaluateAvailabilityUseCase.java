package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.input.EvaluateAvailabilityInput;
import com.example.studycore.application.usecase.student.output.EvaluatedDaysOutput;
import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.ClassroomGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EvaluateAvailabilityUseCase {

    private final StudentGateway studentGateway;
    private final ClassroomGateway classroomGateway;

    private final static Double RATIO_MAX = 0.30;
    private final static int RATIO_MIN = 0;

    public EvaluatedDaysOutput execute(EvaluateAvailabilityInput input) {

        final var contractEndDate = input.startDate().plusMonths(input.contractMonths());

        final var conflictWithRegularClasses = studentGateway.findRecurringConflicts(
                input.teacherId(),
                input.days(),
                input.classTime(),
                input.durationMin(),
                input.startDate(),
                contractEndDate
        );

        final var conflictWithExtraClasses = classroomGateway.findExtraConflicts(
                input.teacherId(),
                input.days(),
                input.classTime(),
                input.durationMin(),
                input.startDate(),
                contractEndDate
        );

        final var availability = input.days().stream()
                .map(day -> buildDayAvailability(
                        day,
                        input.startDate(),
                        contractEndDate,
                        conflictWithRegularClasses,
                        conflictWithExtraClasses
                ))
                .toList();

        final var totalClassesPerDay = calculateTotalClasses(input.startDate(), contractEndDate, input.days().get(0));

        return new EvaluatedDaysOutput(totalClassesPerDay, availability);
    }

    // ── Por dia ───────────────────────────────────────────────────────────────

    private EvaluatedDaysOutput.DayAvailability buildDayAvailability(
            String day,
            LocalDate startDate,
            LocalDate contractEndDate,
            List<Student> recurringStudents,
            List<Classroom> classrooms
    ) {
        final var totalClasses = calculateTotalClasses(startDate, contractEndDate, day);

        final var recurringConflicts = expandRecurringConflicts(day, startDate, contractEndDate, recurringStudents);
        final var extraConflicts = filterExtraConflictsByDay(day, classrooms);

        final var allConflicts = mergeConflicts(recurringConflicts, extraConflicts);
        final var status = calculateStatus(allConflicts.size(), totalClasses);

        return new EvaluatedDaysOutput.DayAvailability(
                day,
                totalClasses,
                allConflicts.size(),
                status,
                allConflicts
        );
    }

    // ── Recorrentes → datas específicas ──────────────────────────────────────

    private List<EvaluatedDaysOutput.Conflict> expandRecurringConflicts(
            String day,
            LocalDate startDate,
            LocalDate contractEndDate,
            List<Student> students
    ) {
        final var dayOfWeek = DayOfWeek.valueOf(day);

        return students.stream()
                .filter(s -> s.getClassDays().contains(day))
                .flatMap(s -> {
                    final var overlapStart = startDate.isAfter(s.getStartDate())
                            ? startDate : s.getStartDate();
                    final var overlapEnd = contractEndDate.isBefore(s.getContractEndDate())
                            ? contractEndDate : s.getContractEndDate();

                    if (overlapStart.isAfter(overlapEnd)) return Stream.empty();

                    return expandDates(overlapStart, overlapEnd, dayOfWeek).stream()
                            .map(date -> new EvaluatedDaysOutput.Conflict(
                                    s.getId().toString(),
                                    s.getId().toString(),
                                    s.getType().toString(),
                                    date,
                                    s.getClassTime(),
                                    s.getClassDuration()
                            ));
                })
                .toList();
    }

    // ── Expande todas as ocorrências de um dia da semana no período ───────────

    private List<LocalDate> expandDates(LocalDate startDate, LocalDate contractEndDate, DayOfWeek dayOfWeek) {
        final List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        while (!current.isAfter(contractEndDate)) {
            dates.add(current);
            current = current.plusWeeks(1);
        }
        return dates;
    }

    // ── Extras → filtra pelo dia da semana ───────────────────────────────────

    private List<EvaluatedDaysOutput.Conflict> filterExtraConflictsByDay(
            String day,
            List<Classroom> classrooms
    ) {
        final var dayOfWeek = DayOfWeek.valueOf(day);

        return classrooms.stream()
                .filter(e -> e.getDate().getDayOfWeek() == dayOfWeek)
                .map(e -> new EvaluatedDaysOutput.Conflict(
                        e.getId().toString(),
                        e.getStudentId().toString(),
                        e.getType().toString(),
                        e.getDate(),
                        e.getStartTime(),
                        e.getDurationMin()
                ))
                .toList();
    }

    // ── Merge sem duplicatas — mesmo dia + mesmo aluno recorrente e avulso ───

    private List<EvaluatedDaysOutput.Conflict> mergeConflicts(
            List<EvaluatedDaysOutput.Conflict> recurring,
            List<EvaluatedDaysOutput.Conflict> extra
    ) {
        final var extraDates = extra.stream()
                .map(EvaluatedDaysOutput.Conflict::date)
                .collect(Collectors.toSet());

        // Remove recorrentes que já estão cobertos por uma aula extra na mesma data
        final var filteredRecurring = recurring.stream()
                .filter(r -> !extraDates.contains(r.date()))
                .toList();

        return Stream.concat(filteredRecurring.stream(), extra.stream())
                .sorted(Comparator.comparing(EvaluatedDaysOutput.Conflict::date))
                .toList();
    }

    // ── Total de aulas no período para um dia ────────────────────────────────

    private int calculateTotalClasses(LocalDate startDate, LocalDate contractEndDate, String day) {
        final var dayOfWeek = DayOfWeek.valueOf(day);
        LocalDate current = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        int count = 0;
        while (!current.isAfter(contractEndDate)) {
            count++;
            current = current.plusWeeks(1);
        }
        return count;
    }

    // ── Status baseado no percentual de conflitos ────────────────────────────

    private String calculateStatus(int conflictCount, int totalClasses) {
        if (totalClasses == 0) return "free";
        final var ratio = (double) conflictCount / totalClasses;
        if (ratio == RATIO_MIN) return "free";
        if (ratio <= RATIO_MAX) return "warn";
        return "block";
    }
}
