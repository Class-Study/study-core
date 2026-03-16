package com.example.studycore.application.usecase.billing.output;

import java.math.BigDecimal;
import java.util.List;

public record BillingDetailOutput(
        BigDecimal hourlyRate,              // Valor por hora (taxa do aluno)
        List<String> classWeekDays,         // Dias da semana com aula (ex: [MONDAY, WEDNESDAY, FRIDAY])
        Integer weeksInMonth,               // Quantidade de semanas no mês
        Integer totalClasses,               // Total de aulas no mês (weeksInMonth × classDaysCount)
        BigDecimal totalAmount              // Valor total a pagar (hourlyRate × totalClasses × durationHours)
) {
}

