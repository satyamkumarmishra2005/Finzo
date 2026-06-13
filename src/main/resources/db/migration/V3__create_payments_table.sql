CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL       PRIMARY KEY,
    sender_id       VARCHAR(100)    NOT NULL,
    receiver_id     VARCHAR(100)    NOT NULL,
    amount          DECIMAL(15,2)   NOT NULL,
    currency        VARCHAR(3)      NOT NULL DEFAULT 'USD',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    description     TEXT,
    transaction_ref VARCHAR(100)    UNIQUE,
    failure_reason  TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMP
);

CREATE INDEX idx_payments_sender     ON payments(sender_id, created_at DESC);
CREATE INDEX idx_payments_receiver   ON payments(receiver_id, created_at DESC);
CREATE INDEX idx_payments_txn_ref    ON payments(transaction_ref);
CREATE INDEX idx_payments_status     ON payments(status);