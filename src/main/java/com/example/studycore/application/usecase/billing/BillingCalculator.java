package com.example.studycore.application.usecase.billing;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculadora de detalhes de aulas para cobrança mensal.
 * Calcula: quantidade de semanas, total de aulas, e valor total a cobrar.
 */
public class BillingCalculator {

    /**
     * Calcula detalhes de aulas para um mês específico considerando a data de início do aluno
     *
     * @param yearMonth Mês/ano (ex: 2026-03)
     * @param classWeekDays Dias da semana com aula (ex: [MONDAY, WEDNESDAY, FRIDAY])
     * @param hourlyRate Valor/hora da aula (taxa do aluno)
     * @param studentStartDate Data de início do aluno (LocalDate)
     * @return Objeto com detalhes: semanas, total aulas, valor total
     */
    public static BillingClassDetails calculateMonthlyClasses(
            LocalDate yearMonth,
            List<DayOfWeek> classWeekDays,
            BigDecimal hourlyRate,
            LocalDate studentStartDate
    ) {
        final var ym = YearMonth.from(yearMonth);
        final var firstDay = ym.atDay(1);
        final var lastDay = ym.atEndOfMonth();

        // Ajustar o primeiro dia considerado para a data de início do aluno, se for dentro do mês
        LocalDate effectiveStart = (studentStartDate != null && studentStartDate.isAfter(firstDay) && !studentStartDate.isAfter(lastDay))
                ? studentStartDate : firstDay;

        int totalClasses = 0;
        for (LocalDate date = effectiveStart; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (classWeekDays.contains(date.getDayOfWeek())) {
                totalClasses++;
            }
        }

        // Calcular número de semanas "ativas" (pode ser parcial se aluno entrou no meio do mês)
        int weeksInMonth = calculateWeeksInMonth(effectiveStart, lastDay, classWeekDays);
        BigDecimal totalAmount = hourlyRate.multiply(BigDecimal.valueOf(totalClasses));

        final var dayNames = classWeekDays.stream()
                .map(DayOfWeek::name)
                .collect(Collectors.toList());

        return new BillingClassDetails(
                hourlyRate,
                dayNames,
                weeksInMonth,
                totalClasses,
                totalAmount
        );
    }

    /**
     * Calcula quantas vezes cada dia da semana ocorre no mês
     */
    private static int calculateWeeksInMonth(
            LocalDate firstDay,
            LocalDate lastDay,
            List<DayOfWeek> classWeekDays
    ) {
        // Contar ocorrências de cada dia da semana no mês
        int maxOccurrences = 0;

        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (classWeekDays.contains(date.getDayOfWeek())) {
                // Contar em qual "semana" do mês este dia cai
                int weekNumber = getWeekNumberInMonth(date);
                maxOccurrences = Math.max(maxOccurrences, weekNumber);
            }
        }

        return maxOccurrences;
    }

    /**
     * Retorna o número da semana dentro do mês (1, 2, 3, 4, 5)
     */
    private static int getWeekNumberInMonth(LocalDate date) {
        return (date.getDayOfMonth() - 1) / 7 + 1;
    }

    /**
     * Classe que encapsula os detalhes de cálculo de aulas
     */
    public static class BillingClassDetails {
        public final BigDecimal hourlyRate;
        public final List<String> classWeekDays;
        public final Integer weeksInMonth;
        public final Integer totalClasses;
        public final BigDecimal totalAmount;

        public BillingClassDetails(
                BigDecimal hourlyRate,
                List<String> classWeekDays,
                Integer weeksInMonth,
                Integer totalClasses,
                BigDecimal totalAmount
        ) {
            this.hourlyRate = hourlyRate;
            this.classWeekDays = classWeekDays;
            this.weeksInMonth = weeksInMonth;
            this.totalClasses = totalClasses;
            this.totalAmount = totalAmount;
        }
    }
}
