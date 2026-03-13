package com.example.studycore.application.usecase.billing.input;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateStudentRateInput(
        UUID teacherId,
        UUID studentId,
        BigDecimal classRate
) {
}

