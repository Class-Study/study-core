package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.mapper.BillingOutputMapper;
import com.example.studycore.application.usecase.billing.input.MarkAsPaidInput;
import com.example.studycore.application.usecase.billing.output.BillingRecordOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkAsPaidUseCase {

    private static final BillingOutputMapper MAPPER = BillingOutputMapper.INSTANCE;

    private final BillingRecordGateway billingRecordGateway;
    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public BillingRecordOutput execute(MarkAsPaidInput input) {
        final var record = billingRecordGateway.findById(input.billingId())
                .orElseThrow(() -> new NotFoundException("Registro de cobrança não encontrado."));

        final var student = studentGateway.findById(record.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da cobrança não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Cobrança não pertence ao professor autenticado.");
        }

        final BillingRecord updated = BillingRecord.with(
                record.getId(),
                record.getStudentId(),
                record.getReferenceMonth(),
                record.getAmount(),
                "PAID",
                OffsetDateTime.now(),
                record.getNotifiedAt(),
                record.getNotifyCount(),
                record.getNotes(),
                record.getCreatedAt()
        );

        final var saved = billingRecordGateway.save(updated);
        final var levelName = student.getLevelProfileId() == null
                ? "-"
                : levelProfileGateway.findById(student.getLevelProfileId()).map(lp -> lp.getName()).orElse("-");

        return MAPPER.toBillingRecordOutput(saved, student.getName(), levelName);
    }
}

