CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    transaction_id BIGINT,

    type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_notifications_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES wallet_transactions (id)
        ON DELETE SET NULL,

    CONSTRAINT chk_notifications_status
        CHECK  (status IN ('UNREAD', 'READ'))
);

CREATE INDEX idx_notifications_user_status_created
    ON notifications(user_id, status, created_at DESC)