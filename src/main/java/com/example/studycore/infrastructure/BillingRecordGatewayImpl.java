package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.infrastructure.mapper.BillingInfraMapper;
import com.example.studycore.infrastructure.persistence.billing.BillingRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BillingRecordGatewayImpl implements BillingRecordGateway {

    private static final BillingInfraMapper BILLING_INFRA_MAPPER = BillingInfraMapper.INSTANCE;

    private final BillingRecordRepository billingRecordRepository;

    @Override
    public BillingRecord save(BillingRecord billingRecord) {
        final var saved = billingRecordRepository.save(BILLING_INFRA_MAPPER.toEntity(billingRecord));
        return BILLING_INFRA_MAPPER.fromEntity(saved);
    }

    @Override
    public Optional<BillingRecord> findById(UUID id) {
        return billingRecordRepository.findById(id).map(BILLING_INFRA_MAPPER::fromEntity);
    }

    @Override
    public Optional<BillingRecord> findByStudentIdAndReferenceMonth(UUID studentId, LocalDate referenceMonth) {
        return billingRecordRepository.findByStudentIdAndReferenceMonth(studentId, referenceMonth)
                .map(BILLING_INFRA_MAPPER::fromEntity);
    }

    @Override
    public List<BillingRecord> findByStudentIdsAndReferenceMonth(Set<UUID> studentIds, LocalDate referenceMonth) {
        return billingRecordRepository.findByStudentIdInAndReferenceMonth(studentIds, referenceMonth)
                .stream().map(BILLING_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    public List<BillingRecord> findByStudentIdOrderByReferenceMonthDesc(UUID studentId) {
        return billingRecordRepository.findByStudentIdOrderByReferenceMonthDesc(studentId)
                .stream().map(BILLING_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    public List<BillingRecord> findByStudentIdsAndStatusIn(Set<UUID> studentIds, Set<String> status) {
        return billingRecordRepository.findByStudentIdInAndStatusIn(studentIds, status)
                .stream().map(BILLING_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    public List<BillingRecord> findByStatus(String status) {
        return billingRecordRepository.findByStatus(status)
                .stream().map(BILLING_INFRA_MAPPER::fromEntity).toList();
    }

    @Override
    @Transactional
    public void insertMonthlyIfAbsent(UUID studentId, LocalDate referenceMonth, BigDecimal amount) {
        billingRecordRepository.insertMonthlyIfAbsent(studentId, referenceMonth, amount);
    }

    @Override
    @Transactional
    public void updatePendingToOverdue(UUID teacherId, LocalDate referenceMonth, LocalDate now) {
        billingRecordRepository.updatePendingToOverdue(teacherId, referenceMonth);
    }
}
