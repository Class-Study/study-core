package com.example.studycore.application.usecase.billing.input;

import java.util.UUID;

public record MarkAsPaidInput(
        UUID teacherId,
        UUID billingId
) {
}

