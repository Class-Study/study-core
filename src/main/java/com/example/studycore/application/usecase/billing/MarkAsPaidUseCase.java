package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.mapper.BillingOutputMapper;
import com.example.studycore.application.usecase.billing.input.MarkAsPaidInput;
import com.example.studycore.application.usecase.billing.output.BillingRecordOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentBillingGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkAsPaidUseCase {

    private static final BillingOutputMapper MAPPER = BillingOutputMapper.INSTANCE;

    private final BillingRecordGateway billingRecordGateway;
    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final StudentBillingGateway studentBillingGateway;

    @Transactional
    public BillingRecordOutput execute(MarkAsPaidInput input) {
        log.debug("Marcando pagamento | billingId={} | teacherId={}", input.billingId(), input.teacherId());

        final var record = billingRecordGateway.findById(input.billingId())
                .orElseThrow(() -> new NotFoundException("Registro de cobrança não encontrado."));

        final var student = studentGateway.findById(record.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da cobrança não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            log.warn("✗ MarkAsPaid | billingId={} | Autorização negada", input.billingId());
            throw new BusinessException("Cobrança não pertence ao professor autenticado.");
        }

        if ("PAID".equals(record.getStatus())) {
            log.info("✓ MarkAsPaid | billingId={} | Status=PAID (idempotente)", input.billingId());
            return MAPPER.toBillingRecordOutput(record, student.getName(), getLevelName(student));
        }

        final var updated = record.markAsPaid();
        final var saved = billingRecordGateway.save(updated);

        // Sync StudentBilling status to PAID
        final int month = record.getReferenceMonth().getMonthValue();
        final int year = record.getReferenceMonth().getYear();
        studentBillingGateway.findByStudentIdAndMonthAndYear(record.getStudentId(), month, year)
                .ifPresent(sb -> studentBillingGateway.save(sb.markAsPaid()));

        log.info("✓ MarkAsPaid | billingId={} | studentName={}", input.billingId(), student.getName());

        return MAPPER.toBillingRecordOutput(saved, student.getName(), getLevelName(student));
    }

    private String getLevelName(Student student) {
        return student.getLevelProfileId() == null
                ? "-"
                : levelProfileGateway.findById(student.getLevelProfileId())
                .map(LevelProfile::getName)
                .orElse("-");
    }
}

