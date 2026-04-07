CREATE TABLE IF NOT EXISTS study.student_billing
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID          NOT NULL REFERENCES study.students (id),
    month       INTEGER       NOT NULL,
    year        INTEGER       NOT NULL,
    class_count INTEGER       NOT NULL,
    class_value NUMERIC(10, 2) NOT NULL,
    total_value NUMERIC(10, 2) NOT NULL,
    due_date    DATE          NOT NULL,
    status      VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    paid_at     TIMESTAMPTZ,
    CONSTRAINT chk_student_billing_status CHECK (status IN ('PENDING', 'OVERDUE', 'PAID', 'AWAITING_CONFIRMATION')),
    CONSTRAINT uq_student_billing_student_month UNIQUE (student_id, month, year)
);

CREATE INDEX IF NOT EXISTS idx_student_billing_student_id ON study.student_billing (student_id);
CREATE INDEX IF NOT EXISTS idx_student_billing_status ON study.student_billing (status);



