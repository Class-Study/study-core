CREATE TABLE student_notes
(
    id         UUID PRIMARY KEY,
    student_id UUID        NOT NULL REFERENCES students (id),
    author_id  UUID        NOT NULL REFERENCES users (id),
    type       VARCHAR(20) NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()

        CONSTRAINT chk_message_type CHECK (type IN ('PRIVATE', 'PUBLIC'))
);