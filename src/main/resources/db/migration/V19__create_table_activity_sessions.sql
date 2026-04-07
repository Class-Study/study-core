CREATE TABLE activity_sessions
(
    id              UUID PRIMARY KEY,
    activity_id     UUID        NOT NULL REFERENCES activities (id),
    user_id         UUID        NOT NULL REFERENCES users (id),
    connected_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    disconnected_at TIMESTAMPTZ
);