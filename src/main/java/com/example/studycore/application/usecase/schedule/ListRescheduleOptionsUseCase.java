package com.example.studycore.application.usecase.schedule;

import com.example.studycore.domain.model.TeacherScheduleConfig;
import com.example.studycore.application.usecase.schedule.output.RescheduleOptionInput;
import com.example.studycore.application.usecase.schedule.output.RescheduleOptionOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.ExtraClass;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.ScheduleType;
import com.example.studycore.domain.port.ExtraClassGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListRescheduleOptionsUseCase {

    private static final int MAX_OPTIONS = 3;
    private static final int MAX_SEARCH_WEEKS = 8;

    private final StudentGateway studentGateway;
    private final ExtraClassGateway extraClassGateway;
    // private final TeacherConfigGateway teacherConfigGateway; // descomentar quando implementar

    public RescheduleOptionOutput execute(RescheduleOptionInput input) {

        // 1. Resolve a aula alvo
        LocalDate originalDate;
        LocalTime originalTime;
        int durationMin;
        String studentName;

        if (input.scheduleType() == ScheduleType.RECURRING) {
            Student student = getStudent(input.schedulerId());
            originalDate = input.date();
            originalTime = student.getClassTime();
            durationMin = student.getClassDuration();
            studentName = student.getName();
        } else {
            ExtraClass extraClass = extraClassGateway.findById(input.schedulerId())
                    .orElseThrow(() -> new NotFoundException("Schedule not found"));
            originalDate = extraClass.getDate();
            originalTime = extraClass.getStartTime();
            durationMin = extraClass.getDurationMin();
            studentName = getStudent(extraClass.getStudentId()).getName();
        }

        // 2. Carrega config do professor — mock até implementar a tabela
        TeacherScheduleConfig config = loadConfig(input.teacherId());

        // 3. Varre slots
        List<RescheduleOptionOutput.Option> options = new ArrayList<>();
        LocalDate candidateDate = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDate searchUntil = candidateDate.plusWeeks(MAX_SEARCH_WEEKS);

        final LocalDate fOriginalDate = originalDate;
        final LocalTime fOriginalTime = originalTime;
        final int fDurationMin = durationMin;

        outer:
        while (!candidateDate.isAfter(searchUntil)) {

            // Pula dias que o professor não trabalha ou tem bloqueio
            if (!config.getWorkDays().contains(candidateDate.getDayOfWeek())
                    || config.getBlockedDates().contains(candidateDate)) {
                candidateDate = candidateDate.plusDays(1);
                continue;
            }

            for (TeacherScheduleConfig.WorkBlock block : config.getWorkBlocks()) {
                LocalTime slotStart = resolveFirstSlot(block, fDurationMin, candidateDate, now);

                while (!slotStart.plusMinutes(fDurationMin).isAfter(block.end())) {
                    LocalTime slotEnd = slotStart.plusMinutes(fDurationMin);

                    boolean isOriginalSlot = candidateDate.equals(fOriginalDate)
                            && slotStart.equals(fOriginalTime);

                    if (!isOriginalSlot && isSlotFree(input.teacherId(), candidateDate, slotStart, slotEnd)) {
                        options.add(new RescheduleOptionOutput.Option(candidateDate, slotStart));
                        if (options.size() == MAX_OPTIONS) break outer;
                    }

                    slotStart = slotStart.plusMinutes(fDurationMin);
                }
            }

            candidateDate = candidateDate.plusDays(1);
        }

        // 4. Monta o output
        return new RescheduleOptionOutput(
                input.schedulerId(),
                studentName,
                originalDate,
                durationMin,
                options,
                options.size() < MAX_OPTIONS
        );
    }

    // Substitui por teacherConfigGateway.findByTeacherId(teacherId) quando implementar
    private TeacherScheduleConfig loadConfig(UUID teacherId) {
        return TeacherScheduleConfig.defaultConfig();
    }

    private LocalTime resolveFirstSlot(TeacherScheduleConfig.WorkBlock block, int durationMin, LocalDate date, LocalTime now) {
        if (!date.equals(LocalDate.now()) || now.isBefore(block.start())) {
            return block.start();
        }
        if (!now.isBefore(block.end())) {
            return block.end().plusMinutes(durationMin); // força skip
        }
        long minutesPassed = Duration.between(block.start(), now).toMinutes();
        long slotsPassed = (minutesPassed / durationMin) + 1;
        LocalTime calculated = block.start().plusMinutes(slotsPassed * durationMin);

        if (calculated.plusMinutes(durationMin).isAfter(block.end())) {
            return block.end().plusMinutes(durationMin); // força skip
        }
        return calculated;
    }

    private boolean isSlotFree(UUID teacherId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !extraClassGateway.existsByTeacherAndTimeOverlap(teacherId, date, startTime, endTime)
                && !studentGateway.existsRecurringOverlapOnDate(teacherId, date, startTime, endTime);
    }

    private Student getStudent(UUID id) {
        return studentGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }
}
