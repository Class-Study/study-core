package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.mapper.BillingOutputMapper;
import com.example.studycore.application.usecase.billing.output.NotifyOutput;
import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.EmailNotification;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.EmailNotificationGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyPendingUseCase {

    private static final BillingOutputMapper MAPPER = BillingOutputMapper.INSTANCE;

    private final BillingRecordGateway billingRecordGateway;
    private final EmailNotificationGateway emailNotificationGateway;
    private final StudentGateway studentGateway;
    private final NotifyEmailService notifyEmailService;

    @Transactional
    public NotifyOutput execute(UUID teacherId) {
        log.debug("Iniciando notificação de pendências | teacherId={}", teacherId);

        final var students = studentGateway.findAllByTeacherId(teacherId).stream()
                .filter(s -> s.getStatus() == UserStatus.ACTIVE)
                .toList();

        final var studentIds = students.stream().map(Student::getId).collect(Collectors.toSet());
        if (studentIds.isEmpty()) {
            log.info("✓ NotifyPending | teacherId={} | students=0 | enviadas=0", teacherId);
            return new NotifyOutput(0, "Nenhuma notificação pendente.");
        }

        final var pending = billingRecordGateway.findByStudentIdsAndStatusIn(studentIds, Set.of("PENDING", "OVERDUE"));

        int sent = 0;
        for (BillingRecord record : pending) {
            final var student = students.stream()
                    .filter(s -> s.getId().equals(record.getStudentId()))
                    .findFirst()
                    .orElse(null);
            if (student == null) {
                continue;
            }

            try {
                notifyEmailService.sendBillingNotification(
                        student.getEmail(),
                        student.getName(),
                        record.getAmount(),
                        MAPPER.formatReferenceMonth(record.getReferenceMonth())
                );

                final var updated = BillingRecord.with(
                        record.getId(),
                        record.getStudentId(),
                        record.getReferenceMonth(),
                        record.getDueDate(),
                        record.getAmount(),
                        record.getAmountAtBillingTime(),
                        record.getStatus(),
                        record.getPaidAt(),
                        OffsetDateTime.now(),
                        (record.getNotifyCount() == null ? 0 : record.getNotifyCount()) + 1,
                        record.getNotes(),
                        record.getCreatedAt()
                );
                billingRecordGateway.save(updated);

                emailNotificationGateway.save(EmailNotification.create(
                        record.getId(),
                        student.getEmail(),
                        "EduSpace — Lembrete de pagamento",
                        "SENT",
                        null
                ));

                sent++;
                log.debug("Notificação enviada | studentId={} | email={}", student.getId(), student.getEmail());
            } catch (Exception e) {
                log.warn("Erro ao enviar notificação | studentId={} | email={} | erro={}",
                        student.getId(), student.getEmail(), e.getMessage());
            }
        }

        log.info("✓ NotifyPending | teacherId={} | total_pendentes={} | enviadas={}",
                teacherId, pending.size(), sent);

        return new NotifyOutput(sent, sent + " notificações enviadas com sucesso.");
    }
}


