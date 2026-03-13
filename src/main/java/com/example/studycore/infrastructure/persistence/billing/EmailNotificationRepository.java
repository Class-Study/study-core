package com.example.studycore.infrastructure.persistence.billing;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailNotificationRepository extends JpaRepository<EmailNotificationEntity, UUID> {
}

