package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.billing.output.BillingMonthOutput;
import com.example.studycore.application.usecase.billing.output.BillingRecordOutput;
import com.example.studycore.domain.model.BillingRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BillingOutputMapper {

    BillingOutputMapper INSTANCE = Mappers.getMapper(BillingOutputMapper.class);

    default BillingRecordOutput toBillingRecordOutput(BillingRecord record, String studentName, String levelProfileName) {
        return new BillingRecordOutput(
                record.getId(),
                record.getStudentId(),
                studentName,
                levelProfileName,
                record.getAmount(),
                record.getStatus(),
                record.getPaidAt() == null ? null : record.getPaidAt().toInstant(),
                record.getNotifyCount(),
                formatReferenceMonth(record.getReferenceMonth())
        );
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
        if (referenceMonth == null) return null;
        return referenceMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
    }
}

