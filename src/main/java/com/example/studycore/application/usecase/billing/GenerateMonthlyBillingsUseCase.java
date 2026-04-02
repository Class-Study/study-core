package com.example.studycore.application.usecase.billing;

import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.StudentBilling;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.StudentBillingGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateMonthlyBillingsUseCase {

    private final BillingRecordGateway billingRecordGateway;
    private final StudentBillingGateway studentBillingGateway;
    private final StudentGateway studentGateway;

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
            final var existingBilling = billingRecordGateway
                    .findByStudentIdAndReferenceMonth(student.getId(), referenceMonth);

            if (existingBilling.isEmpty()) {
                final var dueDate = referenceMonth.plusDays(14);
                final var classRate = student.getClassRate() != null ? student.getClassRate() : BigDecimal.ZERO;

                // Calculate class details using BillingCalculator
                final var classDetails = calculateClassDetails(student.getClassDays(), classRate, referenceMonth, student.getStartDate());

                // Create BillingRecord (teacher-facing)
                final var billingRecord = BillingRecord.create(
                        student.getId(),
                        referenceMonth,
                        dueDate,
                        classDetails.totalAmount,
                        "Cobrança automática gerada no início do mês"
                );
                billingRecordGateway.save(billingRecord);

                // Create StudentBilling (student-facing)
                final var existingStudentBilling = studentBillingGateway
                        .findByStudentIdAndMonthAndYear(student.getId(), referenceMonth.getMonthValue(), referenceMonth.getYear());

                if (existingStudentBilling.isEmpty()) {
                    final var studentBilling = StudentBilling.create(
                            student.getId(),
                            referenceMonth.getMonthValue(),
                            referenceMonth.getYear(),
                            classDetails.totalClasses,
                            classRate,
                            classDetails.totalAmount,
                            dueDate
                    );
                    studentBillingGateway.save(studentBilling);
                }

                createdCount++;
                log.debug("Cobrança criada | studentId={} | studentName={} | classes={} | amount={}",
                        student.getId(), student.getName(), classDetails.totalClasses, classDetails.totalAmount);
            }
        }

        log.info("✓ GenerateMonthlyBillings | teacherId={} | month={} | total_alunos={} | criadas={}",
                teacherId, referenceMonth, students.size(), createdCount);

        return createdCount;
    }

    private BillingCalculator.BillingClassDetails calculateClassDetails(
            List<String> classDays,
            BigDecimal classRate,
            LocalDate referenceMonth,
            LocalDate studentStartDate
    ) {
        if (classDays == null || classDays.isEmpty()) {
            return new BillingCalculator.BillingClassDetails(classRate, List.of(), 0, 0, BigDecimal.ZERO);
        }

        final var dayOfWeeks = classDays.stream()
                .map(d -> {
                    try { return DayOfWeek.valueOf(d.toUpperCase()); }
                    catch (IllegalArgumentException e) { return null; }
                })
                .filter(java.util.Objects::nonNull)
                .toList();

        if (dayOfWeeks.isEmpty()) {
            return new BillingCalculator.BillingClassDetails(classRate, List.of(), 0, 0, BigDecimal.ZERO);
        }

        return BillingCalculator.calculateMonthlyClasses(referenceMonth, dayOfWeeks, classRate, studentStartDate);
    }
}



