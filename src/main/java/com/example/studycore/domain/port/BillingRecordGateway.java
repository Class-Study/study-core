package com.example.studycore.domain.port;

import com.example.studycore.domain.model.BillingRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BillingRecordGateway {
    BillingRecord save(BillingRecord billingRecord);
    Optional<BillingRecord> findById(UUID id);
    Optional<BillingRecord> findByStudentIdAndReferenceMonth(UUID studentId, LocalDate referenceMonth);
    List<BillingRecord> findByStudentIdsAndReferenceMonth(Set<UUID> studentIds, LocalDate referenceMonth);
    List<BillingRecord> findByStudentIdOrderByReferenceMonthDesc(UUID studentId);
    List<BillingRecord> findByStudentIdsAndStatusIn(Set<UUID> studentIds, Set<String> status);
    List<BillingRecord> findByStatus(String status);
    void insertMonthlyIfAbsent(UUID studentId, LocalDate referenceMonth, java.math.BigDecimal amount);
}

