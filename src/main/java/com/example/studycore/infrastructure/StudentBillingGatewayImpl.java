package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.StudentBilling;
import com.example.studycore.domain.port.StudentBillingGateway;
import com.example.studycore.infrastructure.mapper.BillingInfraMapper;
import com.example.studycore.infrastructure.persistence.billing.StudentBillingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentBillingGatewayImpl implements StudentBillingGateway {

    private static final BillingInfraMapper MAPPER = BillingInfraMapper.INSTANCE;

    private final StudentBillingRepository studentBillingRepository;

    @Override
    public StudentBilling save(StudentBilling studentBilling) {
        final var saved = studentBillingRepository.save(MAPPER.toStudentBillingEntity(studentBilling));
        return MAPPER.fromStudentBillingEntity(saved);
    }

    @Override
    public List<StudentBilling> findAllByStudentId(UUID studentId) {
        return studentBillingRepository.findAllByStudentId(studentId)
                .stream()
                .map(MAPPER::fromStudentBillingEntity)
                .toList();
    }

    @Override
    public Optional<StudentBilling> findByStudentIdAndMonthAndYear(UUID studentId, Integer month, Integer year) {
        return studentBillingRepository.findByStudentIdAndMonthAndYear(studentId, month, year)
                .map(MAPPER::fromStudentBillingEntity);
    }
}

