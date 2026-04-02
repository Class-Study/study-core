package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.billing.output.BillingMonthOutput;
import com.example.studycore.application.usecase.billing.output.BillingRecordOutput;
import com.example.studycore.application.usecase.billing.output.NotifyOutput;
import com.example.studycore.domain.model.BillingRecord;
import com.example.studycore.domain.model.EmailNotification;
import com.example.studycore.domain.model.StudentBilling;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingMonthResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingRecordResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.NotifyResponse;
import com.example.studycore.infrastructure.persistence.billing.BillingRecordEntity;
import com.example.studycore.infrastructure.persistence.billing.EmailNotificationEntity;
import com.example.studycore.infrastructure.persistence.billing.StudentBillingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BillingInfraMapper {

    BillingInfraMapper INSTANCE = Mappers.getMapper(BillingInfraMapper.class);

    default BillingRecord fromEntity(BillingRecordEntity entity) {
        if (entity == null) return null;
        return BillingRecord.with(
                entity.getId(),
                entity.getStudentId(),
                entity.getReferenceMonth(),
                entity.getDueDate(),
                entity.getAmount(),
                entity.getAmountAtBillingTime(),
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getNotifiedAt(),
                entity.getNotifyCount(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "referenceMonth", source = "referenceMonth")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "amountAtBillingTime", source = "amountAtBillingTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "paidAt", source = "paidAt")
    @Mapping(target = "notifiedAt", source = "notifiedAt")
    @Mapping(target = "notifyCount", source = "notifyCount")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdAt", source = "createdAt")
    BillingRecordEntity toEntity(BillingRecord billingRecord);

    default EmailNotification fromEntity(EmailNotificationEntity entity) {
        if (entity == null) return null;
        return EmailNotification.with(
                entity.getId(),
                entity.getBillingRecordId(),
                entity.getRecipientEmail(),
                entity.getSubject(),
                entity.getStatus(),
                entity.getSentAt(),
                entity.getProviderId()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "billingRecordId", source = "billingRecordId")
    @Mapping(target = "recipientEmail", source = "recipientEmail")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "sentAt", source = "sentAt")
    @Mapping(target = "providerId", source = "providerId")
    EmailNotificationEntity toEntity(EmailNotification emailNotification);

    BillingRecordResponse toBillingRecordResponse(BillingRecordOutput output);
    BillingMonthResponse toBillingMonthResponse(BillingMonthOutput output);
    NotifyResponse toNotifyResponse(NotifyOutput output);

    default StudentBilling fromStudentBillingEntity(StudentBillingEntity entity) {
        if (entity == null) return null;
        return StudentBilling.with(
                entity.getId(),
                entity.getStudentId(),
                entity.getMonth(),
                entity.getYear(),
                entity.getClassCount(),
                entity.getClassValue(),
                entity.getTotalValue(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getPaidAt()
        );
    }

    default StudentBillingEntity toStudentBillingEntity(StudentBilling billing) {
        if (billing == null) return null;
        final var entity = new StudentBillingEntity();
        entity.setId(billing.getId());
        entity.setStudentId(billing.getStudentId());
        entity.setMonth(billing.getMonth());
        entity.setYear(billing.getYear());
        entity.setClassCount(billing.getClassCount());
        entity.setClassValue(billing.getClassValue());
        entity.setTotalValue(billing.getTotalValue());
        entity.setDueDate(billing.getDueDate());
        entity.setStatus(billing.getStatus());
        entity.setCreatedAt(billing.getCreatedAt());
        entity.setPaidAt(billing.getPaidAt());
        return entity;
    }
}

