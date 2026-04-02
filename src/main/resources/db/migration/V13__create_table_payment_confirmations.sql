CREATE TABLE IF NOT EXISTS study.payment_confirmations
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    billing_id           UUID          NOT NULL REFERENCES study.student_billing (id),
    payment_method       VARCHAR(20)   NOT NULL,
    pix_key              VARCHAR(255)  NOT NULL,
    amount               NUMERIC(10, 2) NOT NULL,
    confirmed_by_teacher BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);



