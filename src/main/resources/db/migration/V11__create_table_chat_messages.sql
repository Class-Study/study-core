CREATE TABLE chat_messages
(
    id                UUID PRIMARY KEY,
    activity_id       UUID         NOT NULL REFERENCES activities (id),
    user_id           UUID         NOT NULL REFERENCES users (id),
    content           TEXT         NOT NULL,
    sent_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    read_at           TIMESTAMPTZ
);