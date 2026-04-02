package com.example.studycore.application.usecase.classroom;

import com.example.studycore.application.usecase.classroom.output.GetStudentScheduleOutput;
import com.example.studycore.application.usecase.classroom.output.GetStudentScheduleOutput.ClassItemOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.ScheduleType;
import com.example.studycore.domain.port.ClassroomGateway;
import com.example.studycore.domain.port.StudentBillingGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetStudentScheduleUseCase {

    private final StudentGateway studentGateway;
    private final TeacherGateway teacherGateway;
    private final ClassroomGateway classroomGateway;
    private final StudentBillingGateway studentBillingGateway;

    public GetStudentScheduleOutput execute(UUID studentId) {
        final var today = LocalDate.now();

        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        final var teacher = teacherGateway.findById(student.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Professor não encontrado."));

        final LocalDate contractStart = student.getStartDate();
        final LocalDate contractEnd = student.getContractEndDate();

        // --- Visibility: check if current month's billing is PAID ---
        final boolean currentMonthPaid = studentBillingGateway
                .findByStudentIdAndMonthAndYear(studentId, today.getMonthValue(), today.getYear())
                .map(b -> "PAID".equals(b.getStatus()))
                .orElse(false);

        final LocalDate nextMonthRef = today.plusMonths(1);

        final List<ClassItemOutput> allClasses = new ArrayList<>();

        // --- 1. Generate RECURRING slots on-the-fly ---
        if (contractStart != null && contractEnd != null
                && student.getClassDays() != null && !student.getClassDays().isEmpty()
                && student.getClassTime() != null) {

            final List<DayOfWeek> classDaysList = student.getClassDays().stream()
                    .map(d -> {
                        try {
                            return DayOfWeek.valueOf(d.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            for (LocalDate d = contractStart; !d.isAfter(contractEnd); d = d.plusDays(1)) {
                if (classDaysList.contains(d.getDayOfWeek())) {
                    allClasses.add(new ClassItemOutput(
                            generateRecurringId(studentId, d),
                            d,
                            student.getClassTime(),
                            computeStatus(d, today),
                            "RECORRENTE",
                            false
                    ));
                }
            }
        }

        // --- 2. Add stored EXTRA / RECOVERY classrooms ---
        if (contractStart != null && contractEnd != null) {
            classroomGateway.findByStudentIdAndDateBetween(studentId, contractStart, contractEnd)
                    .stream()
                    .filter(c -> c.getType() != ScheduleType.RECURRING)
                    .forEach(c -> allClasses.add(new ClassItemOutput(
                            c.getId(),
                            c.getDate(),
                            c.getStartTime(),
                            computeStatus(c.getDate(), today),
                            mapClassType(c.getType()),
                            false
                    )));
        }

        // --- 3. Apply visibility filter ---
        final List<ClassItemOutput> visible = allClasses.stream()
                .filter(c -> isVisible(c.date(), today, nextMonthRef, currentMonthPaid))
                .sorted(Comparator.comparing(ClassItemOutput::date).thenComparing(ClassItemOutput::time))
                .collect(Collectors.toCollection(ArrayList::new));

        // --- 4. Find isNextClass (first future class, not today) ---
        final UUID nextClassId = visible.stream()
                .filter(c -> c.date().isAfter(today) && !"CANCELLED".equals(c.status()))
                .findFirst()
                .map(ClassItemOutput::id)
                .orElse(null);

        // --- 5. Mark isNextClass ---
        final List<ClassItemOutput> result = visible.stream()
                .map(c -> new ClassItemOutput(
                        c.id(),
                        c.date(),
                        c.time(),
                        c.status(),
                        c.classType(),
                        nextClassId != null && nextClassId.equals(c.id())
                ))
                .toList();

        return new GetStudentScheduleOutput(
                student.getName(),
                student.getClassDays() != null ? student.getClassDays() : List.of(),
                student.getClassTime(),
                student.getClassDuration(),
                contractStart,
                contractEnd,
                teacher.getName(),
                result
        );
    }

    private String computeStatus(LocalDate date, LocalDate today) {
        if (date.equals(today)) return "TODAY";
        if (date.isBefore(today)) return "COMPLETED";
        return "SCHEDULED";
    }

    private String mapClassType(ScheduleType type) {
        if (type == null) return "RECORRENTE";
        return switch (type) {
            case EXTRA -> "EXTRA";
            case RECOVERY -> "REMARCADA";
            default -> "RECORRENTE";
        };
    }

    private boolean isVisible(LocalDate date, LocalDate today, LocalDate nextMonthRef, boolean currentMonthPaid) {
        // 1. Passado e hoje: sempre visíveis
        if (!date.isAfter(today)) return true;

        // 2. Mês corrente: SEMPRE visível (independente de pagamento)
        if (date.getYear() == today.getYear()
                && date.getMonthValue() == today.getMonthValue()) return true;

        // 3. Próximo mês: visível apenas se mês atual estiver pago
        return date.getYear() == nextMonthRef.getYear()
                && date.getMonthValue() == nextMonthRef.getMonthValue()
                && currentMonthPaid;
    }

    private UUID generateRecurringId(UUID studentId, LocalDate date) {
        final String seed = studentId.toString() + ":" + date + ":RECURRING";
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}
