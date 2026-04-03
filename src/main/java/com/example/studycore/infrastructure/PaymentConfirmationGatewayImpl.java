package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.PaymentConfirmation;
import com.example.studycore.domain.port.PaymentConfirmationGateway;
import com.example.studycore.infrastructure.mapper.BillingInfraMapper;
import com.example.studycore.infrastructure.persistence.billing.PaymentConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConfirmationGatewayImpl implements PaymentConfirmationGateway {

    private static final BillingInfraMapper MAPPER = BillingInfraMapper.INSTANCE;

    private final PaymentConfirmationRepository paymentConfirmationRepository;

    @Override
    public PaymentConfirmation save(PaymentConfirmation confirmation) {
        final var saved = paymentConfirmationRepository.save(MAPPER.toPaymentConfirmationEntity(confirmation));
        return MAPPER.fromPaymentConfirmationEntity(saved);
    }
}

