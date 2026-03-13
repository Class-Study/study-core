package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.EmailNotification;
import com.example.studycore.domain.port.EmailNotificationGateway;
import com.example.studycore.infrastructure.mapper.BillingInfraMapper;
import com.example.studycore.infrastructure.persistence.billing.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationGatewayImpl implements EmailNotificationGateway {

    private static final BillingInfraMapper BILLING_INFRA_MAPPER = BillingInfraMapper.INSTANCE;

    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    public EmailNotification save(EmailNotification emailNotification) {
        final var saved = emailNotificationRepository.save(BILLING_INFRA_MAPPER.toEntity(emailNotification));
        return BILLING_INFRA_MAPPER.fromEntity(saved);
    }
}

