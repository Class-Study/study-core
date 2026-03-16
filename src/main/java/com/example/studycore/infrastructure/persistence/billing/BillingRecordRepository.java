package com.example.studycore.infrastructure.persistence.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, UUID> {

    Optional<BillingRecordEntity> findById(UUID id);

    Optional<BillingRecordEntity> findByStudentIdAndReferenceMonth(UUID studentId, LocalDate referenceMonth);

    List<BillingRecordEntity> findByStudentIdInAndReferenceMonth(Set<UUID> studentIds, LocalDate referenceMonth);

    List<BillingRecordEntity> findByStudentIdOrderByReferenceMonthDesc(UUID studentId);

    List<BillingRecordEntity> findByStudentIdInAndStatusIn(Set<UUID> studentIds, Set<String> statuses);

    List<BillingRecordEntity> findByStatus(String status);

    @Modifying
    @Query(value = """
            INSERT INTO study.billing_records (student_id, reference_month, amount, status, notify_count)
            VALUES (:studentId, :referenceMonth, :amount, 'PENDING', 0)
            ON CONFLICT (student_id, reference_month) DO NOTHING
            """, nativeQuery = true)
    void insertMonthlyIfAbsent(
            @Param("studentId") UUID studentId,
            @Param("referenceMonth") LocalDate referenceMonth,
            @Param("amount") BigDecimal amount
    );

    @Modifying
    @Query(value = """
        UPDATE study.billing_records br
        SET status = 'OVERDUE'
        WHERE br.status = 'PENDING'
          AND br.reference_month = :referenceMonth
          AND br.student_id IN (
            SELECT s.id FROM study.students s WHERE s.teacher_id = :teacherId
          )
    """, nativeQuery = true)
    void updatePendingToOverdue(
        @Param("teacherId") UUID teacherId,
        @Param("referenceMonth") LocalDate referenceMonth
    );
}
