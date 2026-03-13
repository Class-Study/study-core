CREATE TABLE folders
(
    id             UUID PRIMARY KEY,
    student_id     UUID          NOT NULL REFERENCES students (id),
    name           VARCHAR(100)  NOT NULL,
    position       INT      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);