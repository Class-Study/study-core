package com.example.studycore.application.job;

import com.example.studycore.domain.port.BillingRecordGateway;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingOverdueCheckJob {

    private final BillingRecordGateway billingRecordGateway;

    /**
     * Job que roda diariamente à meia-noite (00:00) para verificar cobranças atrasadas.
     * Atualiza o status de PENDING para OVERDUE se a dueDate passou.
     * Cron: "0 0 0 * * ?" = 00:00:00 diariamente
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkOverdueRecords() {
        log.info("Iniciando verificação de cobranças atrasadas...");

        final var today = LocalDate.now();
        final var pendingRecords = billingRecordGateway.findByStatus("PENDING");

        int updatedCount = 0;
        for (final var record : pendingRecords) {
            // Se a data de vencimento passou, atualizar para OVERDUE
            if (record.getDueDate() != null && record.getDueDate().isBefore(today)) {
                // Criar novo BillingRecord com status OVERDUE
                final var overdueRecord = com.example.studycore.domain.model.BillingRecord.with(
                        record.getId(),
                        record.getStudentId(),
                        record.getReferenceMonth(),
                        record.getDueDate(),
                        record.getAmount(),
                        record.getAmountAtBillingTime(),
                        "OVERDUE",
                        record.getPaidAt(),
                        record.getNotifiedAt(),
                        record.getNotifyCount(),
                        record.getNotes(),
                        record.getCreatedAt()
                );
                billingRecordGateway.save(overdueRecord);
                updatedCount++;
                log.debug("Registro marcado como OVERDUE | billingId={} | studentId={} | daysOverdue={}",
                        record.getId(), record.getStudentId(), record.getDaysOverdue());
            }
        }

        log.info("✓ Verificação de atraso concluída | atualizado={} registros", updatedCount);
    }
}


