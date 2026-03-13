package com.example.studycore.infrastructure.scheduler;

import com.example.studycore.application.usecase.billing.GenerateMonthlyBillingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final GenerateMonthlyBillingUseCase generateMonthlyBillingUseCase;

    @Scheduled(cron = "0 0 8 1 * *")
    public void generateMonthlyBilling() {
        generateMonthlyBillingUseCase.execute();
    }
}

