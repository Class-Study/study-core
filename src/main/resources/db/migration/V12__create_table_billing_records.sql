CREATE TABLE IF NOT EXISTS study.billing_records
(
    id                     UUID PRIMARY KEY       DEFAULT gen_random_uuid(),
    student_id             UUID          NOT NULL REFERENCES study.students (id),
    reference_month        DATE          NOT NULL,
    due_date               DATE          NOT NULL,
    amount                 NUMERIC(8, 2) NOT NULL,
    amount_at_billing_time NUMERIC(8, 2) NOT NULL,
    status                 VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    paid_at                TIMESTAMPTZ,
    notified_at            TIMESTAMPTZ,
    notify_count           INT           NOT NULL DEFAULT 0,
    notes                  TEXT,
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_billing_status CHECK (status IN ('PAID', 'PENDING', 'OVERDUE', 'AWAITING_CONFIRMATION')),
    CONSTRAINT uq_billing_student_month UNIQUE (student_id, reference_month)
);

CREATE INDEX IF NOT EXISTS idx_billing_student_id ON study.billing_records (student_id);
CREATE INDEX IF NOT EXISTS idx_billing_status ON study.billing_records (status);
CREATE INDEX IF NOT EXISTS idx_billing_reference_month ON study.billing_records (reference_month);
CREATE INDEX IF NOT EXISTS idx_billing_due_date_status ON study.billing_records (due_date, status) WHERE status != 'PAID';
CREATE INDEX IF NOT EXISTS idx_billing_student_month ON study.billing_records (student_id, reference_month DESC);

CREATE TABLE IF NOT EXISTS study.student_billing
(
    id          UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    student_id  UUID           NOT NULL REFERENCES study.students (id),
    month       INTEGER        NOT NULL,
    year        INTEGER        NOT NULL,
    class_count INTEGER        NOT NULL,
    class_value NUMERIC(10, 2) NOT NULL,
    total_value NUMERIC(10, 2) NOT NULL,
    due_date    DATE           NOT NULL,
    status      VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    paid_at     TIMESTAMPTZ,
    CONSTRAINT chk_student_billing_status CHECK (status IN ('PENDING', 'OVERDUE', 'PAID', 'AWAITING_CONFIRMATION')),
    CONSTRAINT uq_student_billing_student_month UNIQUE (student_id, month, year)
);

CREATE INDEX IF NOT EXISTS idx_student_billing_student_id ON study.student_billing (student_id);
CREATE INDEX IF NOT EXISTS idx_student_billing_status ON study.student_billing (status);

CREATE TABLE IF NOT EXISTS study.payment_confirmations
(
    id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    billing_id           UUID           NOT NULL REFERENCES study.student_billing (id),
    payment_method       VARCHAR(20)    NOT NULL,
    pix_key              VARCHAR(255)   NOT NULL,
    amount               NUMERIC(10, 2) NOT NULL,
    confirmed_by_teacher BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);
