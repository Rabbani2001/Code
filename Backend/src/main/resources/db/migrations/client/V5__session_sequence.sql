CREATE TABLE sequence_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(30) NOT NULL,
    tenant_code VARCHAR(50) NOT NULL,
    session_key VARCHAR(30) NOT NULL,
    next_value BIGINT NOT NULL,
    UNIQUE KEY uk_tenant_session_key_type (tenant_code, session_key,type)
);