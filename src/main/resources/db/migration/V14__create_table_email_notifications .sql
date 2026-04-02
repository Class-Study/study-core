CREATE TABLE study.email_notifications
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    billing_record_id UUID         NOT NULL REFERENCES study.billing_records (id),
    recipient_email   VARCHAR(255) NOT NULL,
    subject           VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    sent_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    provider_id       TEXT,
    CONSTRAINT chk_email_status CHECK (status IN ('SENT', 'DELIVERED', 'FAILED', 'OPENED'))
);

