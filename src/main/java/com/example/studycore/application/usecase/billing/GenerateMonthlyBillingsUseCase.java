package com.example.studycore.application.usecase.billing;

import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateMonthlyBillingsUseCase {

    private final BillingRecordGateway billingRecordGateway;
    private final StudentGateway studentGateway;

    /**
     * Gera automaticamente as cobranças mensais para todos os alunos ativos.
     * Executado no início de cada mês para criar registros de faturamento.
     * Lógica:
     * 1. Busca todos os alunos ATIVOS
     * 2. Para cada aluno, verifica se já existe cobrança neste mês
     * 3. Se não existir, cria uma nova cobrança com a taxa do aluno
     * 4. A data de vencimento é 15 dias após o início do mês
     */
    @Transactional
    public int execute(UUID teacherId, LocalDate referenceMonth) {
        log.info("Gerando cobranças mensais | teacherId={} | month={}", teacherId, referenceMonth);

        final var students = studentGateway.findAllByTeacherId(teacherId).stream()
                .filter(s -> s.getStatus() == UserStatus.ACTIVE)
                .toList();

        if (students.isEmpty()) {
            log.info("✓ GenerateMonthlyBillings | teacherId={} | students=0 | criadas=0", teacherId);
            return 0;
        }

        int createdCount = 0;

        for (final var student : students) {
            // Verificar se já existe cobrança para este mês
            final var existingBilling = billingRecordGateway
                    .findByStudentIdAndReferenceMonth(student.getId(), referenceMonth);

            if (existingBilling.isEmpty()) {
                // Criar nova cobrança
                final var dueDate = referenceMonth.plusDays(14); // Vencimento: 15 dias após início do mês
                final var billingRecord = BillingRecord.create(
                        student.getId(),
                        referenceMonth,
                        dueDate,
                        student.getClassRate() != null ? student.getClassRate() : BigDecimal.ZERO,
                        "Cobrança automática gerada no início do mês"
                );

                billingRecordGateway.save(billingRecord);
                createdCount++;

                log.debug("Cobrança criada | studentId={} | studentName={} | amount={}",
                        student.getId(), student.getName(), student.getClassRate());
            }
        }

        log.info("✓ GenerateMonthlyBillings | teacherId={} | month={} | total_alunos={} | criadas={}",
                teacherId, referenceMonth, students.size(), createdCount);

        return createdCount;
    }
}



