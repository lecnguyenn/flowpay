
INSERT INTO wallets (
    wallet_number,
    owner_user_id,
    wallet_type,
    currency,
    balance,
    status,
    created_at,
    updated_at
)
SELECT 'SYSTEM-VND', NULL, 'SYSTEM', 'VND',10000000, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE wallet_type = 'SYSTEM' AND currency = 'VND');
