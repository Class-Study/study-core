CREATE TABLE activities
(
    id           UUID PRIMARY KEY,
    folder_id    UUID         NOT NULL REFERENCES folders (id),
    title        VARCHAR(255) NOT NULL,
    type         VARCHAR(100) NOT NULL,
    content_html TEXT         NOT NULL DEFAULT '',
    yjs_state    BYTEA,
    created_by   UUID         NOT NULL REFERENCES users (id),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()

        CONSTRAINT chk_user_role CHECK (type IN ('EXERCISE', 'WORKSPACE'))
);