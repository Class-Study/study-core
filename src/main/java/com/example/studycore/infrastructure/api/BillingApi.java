package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.billing.request.UpdateStudentRateRequest;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingMonthResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.BillingRecordResponse;
import com.example.studycore.infrastructure.api.controllers.billing.response.NotifyResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/billing")
public interface BillingApi {

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<BillingMonthResponse> getCurrentMonth();

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<List<BillingRecordResponse>> getStudentHistory(@PathVariable UUID studentId);

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<BillingRecordResponse> markAsPaid(@PathVariable UUID id);

    @PatchMapping("/students/{studentId}/rate")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> updateStudentRate(@PathVariable UUID studentId, @RequestBody @Valid UpdateStudentRateRequest request);

    @PostMapping("/notify-pending")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<NotifyResponse> notifyPending();
}

