CREATE TABLE students
(
    id               UUID PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    level_profile_id UUID          NOT NULL REFERENCES level_profiles (id),
    teacher_id       UUID          NOT NULL REFERENCES users (id),
    class_days       VARCHAR[]     NOT NULL DEFAULT '{}',
    class_time       TIME          NOT NULL,
    class_duration   SMALLINT      NOT NULL DEFAULT 60,
    class_rate       NUMERIC(8, 2) NOT NULL DEFAULT 0,
    meet_platform    VARCHAR(50),
    meet_link        TEXT,
    start_date       DATE          NOT NULL,
    notes_private    TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);