package com.example.studycore.infrastructure.persistence.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentBillingRepository extends JpaRepository<StudentBillingEntity, UUID> {

    List<StudentBillingEntity> findAllByStudentId(UUID studentId);

    Optional<StudentBillingEntity> findByStudentIdAndMonthAndYear(UUID studentId, Integer month, Integer year);
}

