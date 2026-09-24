CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id BIGINT NOT NULL,

    event_types VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,

    status VARCHAR(20) NOT NULL  DEFAULT 'NEW',
    retry_count INTEGER NOT NULL NOT NULL DEFAULT 0,

    last_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,

    CONSTRAINT chk_outbox_status
        CHECK (status IN ('NEW', 'PUBLISHED', 'FAILED')),

    CONSTRAINT chk_retry_count
        CHECK (retry_count >= 0)
)