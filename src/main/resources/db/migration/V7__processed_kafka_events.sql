CREATE TABLE processed_kafka_events (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(200) NOT NULL,
    partition_number INTEGER NOT NULL,
    kafka_offset BIGINT NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_processed_event_consumer
        UNIQUE (event_id, consumer_group)
)