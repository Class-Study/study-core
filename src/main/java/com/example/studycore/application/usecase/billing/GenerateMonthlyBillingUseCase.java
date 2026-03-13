package com.example.studycore.application.usecase.billing;

import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenerateMonthlyBillingUseCase {

    private static final BigDecimal WEEKS_PER_MONTH = new BigDecimal("4");

    private final BillingRecordGateway billingRecordGateway;
    private final TeacherGateway teacherGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public void execute() {
        final var referenceMonth = LocalDate.now().withDayOfMonth(1);

        final var teachers = teacherGateway.findAll();
        for (var teacher : teachers) {
            final var students = studentGateway.findAllByTeacherId(teacher.getId()).stream()
                    .filter(s -> s.getStatus() == UserStatus.ACTIVE)
                    .toList();

            for (var student : students) {
                final var rate = student.getClassRate() == null ? BigDecimal.ZERO : student.getClassRate();
                final var amount = rate.multiply(WEEKS_PER_MONTH);
                billingRecordGateway.insertMonthlyIfAbsent(student.getId(), referenceMonth, amount);
            }
        }
    }
}

