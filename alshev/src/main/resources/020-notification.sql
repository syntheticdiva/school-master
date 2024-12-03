DROP TABLE IF EXISTS notifications;

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_attempt_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    event_payload TEXT NOT NULL
);

CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_event_id ON notifications(event_id);