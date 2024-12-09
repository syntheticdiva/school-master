CREATE TABLE notification_status (
    id SERIAL PRIMARY KEY,
    notification_type VARCHAR(50) NOT NULL,
    subscriber_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    attempts INT DEFAULT 0
);