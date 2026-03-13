package com.example.studycore.infrastructure.api.controllers.billing;

import com.example.studycore.application.usecase.billing.GetBillingMonthUseCase;
import com.example.studycore.application.usecase.billing.GetStudentBillingHistoryUseCase;
import com.example.studycore.application.usecase.billing.MarkAsPaidUseCase;
import com.example.studycore.application.usecase.billing.NotifyPendingUseCase;
import com.example.studycore.application.usecase.billing.UpdateStudentRateUseCase;
import com.example.studycore.application.usecase.billing.input.MarkAsPaidInput;
import com.example.studycore.application.usecase.billing.input.UpdateStudentRateInput;
import com.example.studycore.infrastructure.api.BillingApi;
import com.example.studycore.infrastructure.api.controllers.billing.request.UpdateStudentRateRequest;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingMonthResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingRecordResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.NotifyResponse;
import com.example.studycore.infrastructure.mapper.BillingInfraMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillingController implements BillingApi {

    private static final BillingInfraMapper BILLING_INFRA_MAPPER = BillingInfraMapper.INSTANCE;

    private final GetBillingMonthUseCase getBillingMonthUseCase;
    private final GetStudentBillingHistoryUseCase getStudentBillingHistoryUseCase;
    private final MarkAsPaidUseCase markAsPaidUseCase;
    private final NotifyPendingUseCase notifyPendingUseCase;
    private final UpdateStudentRateUseCase updateStudentRateUseCase;

    @Override
    public ResponseEntity<BillingMonthResponse> getCurrentMonth() {
        final var output = getBillingMonthUseCase.execute(getAuthenticatedUserId());
        return ResponseEntity.ok(BILLING_INFRA_MAPPER.toBillingMonthResponse(output));
    }

    @Override
    public ResponseEntity<List<BillingRecordResponse>> getStudentHistory(UUID studentId) {
        final var output = getStudentBillingHistoryUseCase.execute(getAuthenticatedUserId(), studentId);
        return ResponseEntity.ok(output.stream().map(BILLING_INFRA_MAPPER::toBillingRecordResponse).toList());
    }

    @Override
    public ResponseEntity<BillingRecordResponse> markAsPaid(UUID id) {
        final var output = markAsPaidUseCase.execute(new MarkAsPaidInput(getAuthenticatedUserId(), id));
        return ResponseEntity.ok(BILLING_INFRA_MAPPER.toBillingRecordResponse(output));
    }

    @Override
    public ResponseEntity<Void> updateStudentRate(UUID studentId, UpdateStudentRateRequest request) {
        updateStudentRateUseCase.execute(new UpdateStudentRateInput(getAuthenticatedUserId(), studentId, request.classRate()));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<NotifyResponse> notifyPending() {
        final var output = notifyPendingUseCase.execute(getAuthenticatedUserId());
        return ResponseEntity.ok(BILLING_INFRA_MAPPER.toNotifyResponse(output));
    }

    private UUID getAuthenticatedUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

