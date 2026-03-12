CREATE TABLE billing_records
(
    id              UUID PRIMARY KEY,
    student_id      UUID          NOT NULL REFERENCES students (id),
    reference_month DATE          NOT NULL,
    amount          NUMERIC(8, 2) NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    paid_at         TIMESTAMPTZ,
    notified_at     TIMESTAMPTZ,
    notify_count    INT           NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()

        CONSTRAINT chk_message_type CHECK (status IN ('PAID', 'PENDING', 'LATE'))
);