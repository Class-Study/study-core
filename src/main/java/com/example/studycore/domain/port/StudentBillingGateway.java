package com.example.studycore.domain.port;

import com.example.studycore.domain.model.StudentBilling;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentBillingGateway {
    StudentBilling save(StudentBilling studentBilling);
    Optional<StudentBilling> findById(UUID id);
    List<StudentBilling> findAllByStudentId(UUID studentId);
    Optional<StudentBilling> findByStudentIdAndMonthAndYear(UUID studentId, Integer month, Integer year);
}
