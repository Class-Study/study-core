CREATE TABLE activity_notes
(
    id          UUID PRIMARY KEY,
    activity_id UUID        NOT NULL REFERENCES activities (id),
    user_id     UUID        NOT NULL REFERENCES users (id),
    content     TEXT        NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);