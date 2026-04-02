package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.usecase.billing.output.GetStudentBillingOutput;
import com.example.studycore.application.usecase.billing.output.GetStudentBillingOutput.MonthlyBillingItemOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.StudentBilling;
import com.example.studycore.domain.port.StudentBillingGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStudentBillingUseCase {

    private static final Set<String> PENDING_STATUSES = Set.of("PENDING", "OVERDUE", "AWAITING_CONFIRMATION");

    private final StudentGateway studentGateway;
    private final TeacherGateway teacherGateway;
    private final StudentBillingGateway studentBillingGateway;

    public GetStudentBillingOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        final var teacher = teacherGateway.findById(student.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Professor não encontrado."));

        final var allBillings = studentBillingGateway.findAllByStudentId(studentId);

        // Separate paid and pending billings
        final var paidBillings = allBillings.stream()
                .filter(b -> "PAID".equals(b.getStatus()))
                .toList();

        // Find the earliest pending/overdue/awaiting_confirmation billing
        final var nextPending = allBillings.stream()
                .filter(b -> PENDING_STATUSES.contains(b.getStatus()))
                .min(Comparator.comparingInt((StudentBilling b) -> b.getYear() * 100 + b.getMonth()))
                .stream()
                .toList();

        // Combine and sort ascending by year+month
        final var filtered = new ArrayList<StudentBilling>();
        filtered.addAll(paidBillings);
        filtered.addAll(nextPending);
        filtered.sort(Comparator.comparingInt((StudentBilling b) -> b.getYear() * 100 + b.getMonth()));

        final var monthlyBilling = filtered.stream()
                .map(b -> new MonthlyBillingItemOutput(
                        b.getId(),
                        b.getMonth(),
                        b.getYear(),
                        String.format("%04d-%02d", b.getYear(), b.getMonth()),
                        b.getClassCount(),
                        b.getClassValue(),
                        b.getTotalValue(),
                        b.getDueDate(),
                        b.getStatus()
                ))
                .toList();

        return new GetStudentBillingOutput(
                student.getName(),
                student.getStartDate(),
                student.getContractEndDate(),
                student.getClassRate(),
                teacher.getName(),
                teacher.getEmail(),
                teacher.getPixKey(),
                monthlyBilling
        );
    }
}


