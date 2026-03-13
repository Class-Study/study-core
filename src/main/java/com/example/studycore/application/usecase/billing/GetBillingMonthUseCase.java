package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.mapper.BillingOutputMapper;
import com.example.studycore.application.usecase.billing.output.BillingMonthOutput;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBillingMonthUseCase {

    private static final BillingOutputMapper MAPPER = BillingOutputMapper.INSTANCE;

    private final BillingRecordGateway billingRecordGateway;
    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;

    public BillingMonthOutput execute(UUID teacherId) {
        final var referenceMonth = LocalDate.now().withDayOfMonth(1);

        final var students = studentGateway.findAllByTeacherId(teacherId).stream()
                .filter(s -> s.getStatus() == UserStatus.ACTIVE)
                .toList();

        final Set<UUID> studentIds = students.stream().map(s -> s.getId()).collect(Collectors.toSet());
        if (studentIds.isEmpty()) {
            return MAPPER.toBillingMonthOutput(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, referenceMonth, java.util.List.of());
        }

        final var byStudent = students.stream().collect(Collectors.toMap(s -> s.getId(), Function.identity()));
        final var records = billingRecordGateway.findByStudentIdsAndReferenceMonth(studentIds, referenceMonth);

        final var outputs = records.stream().map(record -> {
            final var student = byStudent.get(record.getStudentId());
            final var levelName = student != null && student.getLevelProfileId() != null
                    ? levelProfileGateway.findById(student.getLevelProfileId()).map(lp -> lp.getName()).orElse("-")
                    : "-";
            return MAPPER.toBillingRecordOutput(record, student == null ? "-" : student.getName(), levelName);
        }).toList();

        BigDecimal totalReceived = sumByStatus(records, "PAID");
        BigDecimal totalPending = sumByStatus(records, "PENDING");
        BigDecimal totalLate = sumByStatus(records, "LATE");
        BigDecimal totalExpected = records.stream().map(r -> r.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

        int paidCount = countByStatus(records, "PAID");
        int pendingCount = countByStatus(records, "PENDING");
        int lateCount = countByStatus(records, "LATE");

        return MAPPER.toBillingMonthOutput(
                totalReceived,
                totalPending,
                totalLate,
                totalExpected,
                paidCount,
                pendingCount,
                lateCount,
                referenceMonth,
                outputs
        );
    }

    private BigDecimal sumByStatus(java.util.List<com.example.studycore.domain.model.BillingRecord> records, String status) {
        return records.stream().filter(r -> status.equals(r.getStatus())).map(r -> r.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int countByStatus(java.util.List<com.example.studycore.domain.model.BillingRecord> records, String status) {
        return (int) records.stream().filter(r -> status.equals(r.getStatus())).count();
    }
}

