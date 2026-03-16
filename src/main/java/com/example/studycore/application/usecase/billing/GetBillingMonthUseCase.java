package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.mapper.BillingOutputMapper;
import com.example.studycore.application.usecase.billing.output.BillingMonthOutput;
import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetBillingMonthUseCase {

    private static final BillingOutputMapper MAPPER = BillingOutputMapper.INSTANCE;

    private final BillingRecordGateway billingRecordGateway;
    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final GenerateMonthlyBillingsUseCase generateMonthlyBillingsUseCase;

    /**
     * Execute com mês customizado (opcional)
     * Se month for null, usa o mês atual
     * Formato esperado: "YYYY-MM"
     * Fluxo:
     * 1. Valida e parse do mês
     * 2. Gera automaticamente cobranças para alunos que não possuem
     * 3. Busca e retorna todas as cobranças do mês
     */
    public BillingMonthOutput execute(UUID teacherId, String month) {
        final var referenceMonth = month != null && !month.isEmpty()
                ? LocalDate.parse(month + "-01", DateTimeFormatter.ISO_DATE)
                : LocalDate.now().withDayOfMonth(1);

        log.debug("Gerando resumo de faturamento | teacherId={} | month={}", teacherId, referenceMonth);

        // ✅ Gerar automaticamente cobranças para este mês
        final var billingsGenerated = generateMonthlyBillingsUseCase.execute(teacherId, referenceMonth);
        log.debug("Cobranças geradas | teacherId={} | month={} | generated={}",
                teacherId, referenceMonth, billingsGenerated);

        final var students = studentGateway.findAllByTeacherId(teacherId).stream()
                .filter(s -> s.getStatus() == UserStatus.ACTIVE)
                .toList();

        final var studentIds = students.stream().map(Student::getId).collect(Collectors.toSet());
        if (studentIds.isEmpty()) {
            log.info("✓ GetBillingMonth | teacherId={} | students=0", teacherId);
            return MAPPER.toBillingMonthOutput(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0, 0, 0, referenceMonth, java.util.List.of()
            );
        }

        final var byStudent = students.stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        final var records = billingRecordGateway.findByStudentIdsAndReferenceMonth(studentIds, referenceMonth);

        // Mapear registros com cálculo dinâmico de status e daysOverdue
        final var outputs = records.stream().map(record -> {
            final var student = byStudent.get(record.getStudentId());
            final var levelName = student != null && student.getLevelProfileId() != null
                    ? levelProfileGateway.findById(student.getLevelProfileId())
                    .map(LevelProfile::getName).orElse("-")
                    : "-";

            // Passar student para o mapper calcular os detalhes de aulas
            return MAPPER.toBillingRecordOutput(
                    record,
                    student == null ? "-" : student.getName(),
                    levelName,
                    record.getDaysOverdue(),
                    student,
                    referenceMonth
            );
        }).toList();

        // Calcular totais baseado em status real
        BigDecimal totalReceived = sumByStatus(records, "PAID");
        BigDecimal totalPending = sumByStatus(records, "PENDING");
        BigDecimal totalOverdue = sumByStatus(records, "OVERDUE");
        BigDecimal totalExpected = records.stream()
                .map(BillingRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int paidCount = countByStatus(records, "PAID");
        int pendingCount = countByStatus(records, "PENDING");
        int overdueCount = countByStatus(records, "OVERDUE");

        log.info("✓ GetBillingMonth | teacherId={} | month={} | paid={} | pending={} | overdue={} | generated={}",
                teacherId, referenceMonth, paidCount, pendingCount, overdueCount, billingsGenerated);

        return MAPPER.toBillingMonthOutput(
                totalReceived,
                totalPending,
                totalOverdue,
                totalExpected,
                paidCount,
                pendingCount,
                overdueCount,
                referenceMonth,
                outputs
        );
    }

    // Método legado para compatibilidade
    public BillingMonthOutput execute(UUID teacherId) {
        return execute(teacherId, null);
    }

    private BigDecimal sumByStatus(
            List<BillingRecord> records,
            String status
    ) {
        return records.stream()
                .filter(r -> status.equals(r.getStatus()))
                .map(BillingRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int countByStatus(
            List<com.example.studycore.domain.model.BillingRecord> records,
            String status
    ) {
        return (int) records.stream()
                .filter(r -> status.equals(r.getStatus()))
                .count();
    }
}

