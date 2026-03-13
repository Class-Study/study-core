CREATE TABLE IF NOT EXISTS study.billing_records
(
    id              UUID PRIMARY KEY       DEFAULT gen_random_uuid(),
    student_id      UUID          NOT NULL REFERENCES study.students (id),
    reference_month DATE          NOT NULL,
    amount          NUMERIC(8, 2) NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    paid_at         TIMESTAMPTZ,
    notified_at     TIMESTAMPTZ,
    notify_count    INT           NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_billing_status CHECK (status IN ('PAID', 'PENDING', 'LATE')),
    CONSTRAINT uq_billing_student_month UNIQUE (student_id, reference_month)
);

CREATE INDEX IF NOT EXISTS idx_billing_student_id ON study.billing_records (student_id);
CREATE INDEX IF NOT EXISTS idx_billing_status ON study.billing_records (status);
CREATE INDEX IF NOT EXISTS idx_billing_reference_month ON study.billing_records (reference_month);

