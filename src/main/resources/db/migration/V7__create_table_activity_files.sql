CREATE TABLE activity_files
(
    id                UUID PRIMARY KEY,
    activity_id       UUID         NOT NULL REFERENCES activities (id),
    original_filename VARCHAR(255) NOT NULL,
    storage_path      TEXT         NOT NULL,
    converted_html    TEXT,
    file_size_bytes   INTEGER,
    uploaded_by       UUID         NOT NULL REFERENCES users (id),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);