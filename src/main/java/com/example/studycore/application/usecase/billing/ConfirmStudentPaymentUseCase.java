package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.usecase.billing.input.ConfirmPaymentInput;
import com.example.studycore.application.usecase.billing.output.ConfirmPaymentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.ConflictException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.PaymentConfirmation;
import com.example.studycore.domain.port.BillingRecordGateway;
import com.example.studycore.domain.port.PaymentConfirmationGateway;
import com.example.studycore.domain.port.StudentBillingGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmStudentPaymentUseCase {

    private static final Set<String> ELIGIBLE_STATUSES = Set.of("PENDING", "OVERDUE");

    private final StudentBillingGateway studentBillingGateway;
    private final BillingRecordGateway billingRecordGateway;
    private final PaymentConfirmationGateway paymentConfirmationGateway;

    @Transactional
    public ConfirmPaymentOutput execute(ConfirmPaymentInput input) {
        log.debug("Confirmando pagamento | studentId={} | billingId={}", input.studentId(), input.billingId());

        // 1. Buscar StudentBilling pelo ID
        final var studentBilling = studentBillingGateway.findById(input.billingId())
                .orElseThrow(() -> new NotFoundException("Cobrança não encontrada."));

        // 2. Verificar que a cobrança pertence ao aluno do token (404 por segurança)
        if (!input.studentId().equals(studentBilling.getStudentId())) {
            throw new NotFoundException("Cobrança não encontrada.");
        }

        // 3. Validar status elegível (PENDING ou OVERDUE)
        if (!ELIGIBLE_STATUSES.contains(studentBilling.getStatus())) {
            throw new ConflictException("Cobrança já confirmada ou não elegível.");
        }

        // 4. Validar que o valor informado corresponde ao totalValue da cobrança
        if (input.amount().compareTo(studentBilling.getTotalValue()) != 0) {
            throw new BusinessException("Valor informado não corresponde ao valor da cobrança.");
        }

        // 5. Atualizar StudentBilling → AWAITING_CONFIRMATION
        final var updated = studentBilling.markAsAwaitingConfirmation();
        studentBillingGateway.save(updated);

        // 6. Persistir confirmação de pagamento para auditoria
        final var confirmation = PaymentConfirmation.create(
                input.billingId(),
                input.paymentMethod(),
                input.pixKey(),
                input.amount()
        );
        paymentConfirmationGateway.save(confirmation);

        // 7. Sincronizar BillingRecord (tabela do professor) → AWAITING_CONFIRMATION
        final var referenceMonth = LocalDate.of(studentBilling.getYear(), studentBilling.getMonth(), 1);
        billingRecordGateway.findByStudentIdAndReferenceMonth(studentBilling.getStudentId(), referenceMonth)
                .ifPresent(record -> billingRecordGateway.save(record.markAsAwaitingConfirmation()));

        log.info("✓ ConfirmPayment | studentId={} | billingId={} | status=AWAITING_CONFIRMATION",
                input.studentId(), input.billingId());

        return new ConfirmPaymentOutput(
                true,
                "Pagamento enviado para confirmação do professor.",
                "AWAITING_CONFIRMATION"
        );
    }
}

