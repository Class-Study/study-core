package com.example.studycore.infrastructure.persistence.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentConfirmationRepository extends JpaRepository<PaymentConfirmationEntity, UUID> {
}

