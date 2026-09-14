CREATE TABLE wallet_transactions (

    id BIGSERIAL PRIMARY KEY,
    reference_code VARCHAR(50) NOT NULL UNIQUE,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount NUMERIC(19,0) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    initiated_by_user_id BIGINT NOT NULL,
    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_user
        FOREIGN KEY (initiated_by_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_transaction_amount
        CHECK (amount > 0),

    CONSTRAINT chk_transaction_type
        CHECK (type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),

    CONSTRAINT chk_transaction_status
        CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

CREATE TABLE ledger_entries(
    id BIGINT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount NUMERIC(19,0) NOT NULL,

    balance_before NUMERIC(19,0) NOT NULL,
    balance_after NUMERIC(19,0) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ledger_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES wallet_transactions(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_ledger_wallet
        FOREIGN KEY (wallet_id)
        REFERENCES wallets(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_ledger_entry_type
        CHECK (entry_type IN ('DEBIT', 'CREDIT')),

    CONSTRAINT chk_ledger_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_transactions_users
    ON wallet_transactions(initiated_by_user_id);
