package com.example.studycore.domain.port;

import com.example.studycore.domain.model.PaymentConfirmation;

public interface PaymentConfirmationGateway {
    PaymentConfirmation save(PaymentConfirmation confirmation);
}

