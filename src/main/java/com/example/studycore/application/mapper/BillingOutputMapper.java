package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.billing.output.BillingMonthOutput;
import com.example.studycore.application.usecase.billing.output.BillingRecordOutput;
import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.Student;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BillingOutputMapper {

    BillingOutputMapper INSTANCE = Mappers.getMapper(BillingOutputMapper.class);

    /**
     * Novo método que calcula detalhes de aulas a partir do Student
     */
    default BillingRecordOutput toBillingRecordOutput(
            BillingRecord record,
            String studentName,
            String levelProfileName,
            Long daysOverdue,
            Student student,
            LocalDate referenceMonth
    ) {
        // Calcular detalhes de aulas se student tiver dias de aula definidos
        BigDecimal hourlyRate = null;
        List<String> classWeekDays = List.of();
        Integer weeksInMonth = null;
        Integer totalClasses = null;
        BigDecimal totalAmountCalculated = null;

        if (student != null && student.getClassDays() != null && !student.getClassDays().isEmpty()) {
            // Converter String para DayOfWeek
            final var dayOfWeeks = student.getClassDays().stream()
                    .map(dayStr -> {
                        try {
                            return DayOfWeek.valueOf(dayStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            if (!dayOfWeeks.isEmpty()) {
                final var classDetails = calculateMonthlyClasses(
                        referenceMonth,
                        dayOfWeeks,
                        student.getClassRate() != null ? student.getClassRate() : BigDecimal.ZERO
                );
                hourlyRate = (BigDecimal) classDetails.get(0);
                @SuppressWarnings("unchecked")
                List<String> days = (List<String>) classDetails.get(1);
                classWeekDays = days;
                weeksInMonth = (Integer) classDetails.get(2);
                totalClasses = (Integer) classDetails.get(3);
                totalAmountCalculated = (BigDecimal) classDetails.get(4);
            }
        }

        return new BillingRecordOutput(
                record.getId(),
                record.getStudentId(),
                studentName,
                levelProfileName,
                record.getAmount(),
                record.getAmountAtBillingTime(),
                record.getStatus(),
                record.getPaidAt() == null ? null : record.getPaidAt().toInstant(),
                record.getNotifyCount(),
                formatReferenceMonth(record.getReferenceMonth()),
                daysOverdue,
                hourlyRate,
                classWeekDays,
                weeksInMonth,
                totalClasses,
                totalAmountCalculated
        );
    }

    /**
     * Calcula detalhes de aulas para um mês específico
     * Retorna: [hourlyRate, classWeekDays, weeksInMonth, totalClasses, totalAmount]
     */
    default List<Object> calculateMonthlyClasses(
            LocalDate referenceMonth,
            List<DayOfWeek> classWeekDays,
            BigDecimal hourlyRate
    ) {
        final var ym = YearMonth.from(referenceMonth);
        final var firstDay = ym.atDay(1);
        final var lastDay = ym.atEndOfMonth();

        int weeksInMonth = calculateWeeksInMonth(firstDay, lastDay, classWeekDays);
        int totalClasses = weeksInMonth * classWeekDays.size();
        BigDecimal totalAmount = hourlyRate.multiply(BigDecimal.valueOf(totalClasses));

        final var dayNames = classWeekDays.stream()
                .map(DayOfWeek::name)
                .toList();

        return List.of(hourlyRate, dayNames, weeksInMonth, totalClasses, totalAmount);
    }

    /**
     * Calcula quantas vezes cada dia da semana ocorre no mês
     */
    default int calculateWeeksInMonth(
            LocalDate firstDay,
            LocalDate lastDay,
            List<DayOfWeek> classWeekDays
    ) {
        int maxOccurrences = 0;
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (classWeekDays.contains(date.getDayOfWeek())) {
                int weekNumber = (date.getDayOfMonth() - 1) / 7 + 1;
                maxOccurrences = Math.max(maxOccurrences, weekNumber);
            }
        }
        return maxOccurrences;
    }

    default BillingRecordOutput toBillingRecordOutput(
            BillingRecord record,
            String studentName,
            String levelProfileName,
            Long daysOverdue,
            java.math.BigDecimal hourlyRate,
            List<String> classWeekDays,
            Integer weeksInMonth,
            Integer totalClasses,
            java.math.BigDecimal totalAmountCalculated
    ) {
        return new BillingRecordOutput(
                record.getId(),
                record.getStudentId(),
                studentName,
                levelProfileName,
                record.getAmount(),
                record.getAmountAtBillingTime(),
                record.getStatus(),
                record.getPaidAt() == null ? null : record.getPaidAt().toInstant(),
                record.getNotifyCount(),
                formatReferenceMonth(record.getReferenceMonth()),
                daysOverdue,
                hourlyRate,
                classWeekDays,
                weeksInMonth,
                totalClasses,
                totalAmountCalculated
        );
    }

    // Método legado para compatibilidade (sem detalhes de aulas)
    default BillingRecordOutput toBillingRecordOutput(
            BillingRecord record,
            String studentName,
            String levelProfileName,
            Long daysOverdue
    ) {
        return toBillingRecordOutput(
                record, studentName, levelProfileName, daysOverdue,
                null, java.util.List.of(), null, null, null
        );
    }

    // Método legado para compatibilidade
    default BillingRecordOutput toBillingRecordOutput(BillingRecord record, String studentName, String levelProfileName) {
        return toBillingRecordOutput(record, studentName, levelProfileName, record.getDaysOverdue());
    }

    default BillingMonthOutput toBillingMonthOutput(
            java.math.BigDecimal totalReceived,
            java.math.BigDecimal totalPending,
            java.math.BigDecimal totalLate,
            java.math.BigDecimal totalExpected,
            int paidCount,
            int pendingCount,
            int lateCount,
            LocalDate referenceMonth,
            List<BillingRecordOutput> records
    ) {
        return new BillingMonthOutput(
                totalReceived,
                totalPending,
                totalLate,
                totalExpected,
                paidCount,
                pendingCount,
                lateCount,
                formatReferenceMonth(referenceMonth),
                records
        );
    }

    default String formatReferenceMonth(LocalDate referenceMonth) {
        return referenceMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("pt", "BR")));
    }
}

