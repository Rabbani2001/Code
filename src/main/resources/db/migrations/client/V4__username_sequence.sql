CREATE TABLE sequence_username (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_code VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL,
    next_value BIGINT NOT NULL,
    UNIQUE KEY uk_tenant_role (tenant_code, role)
);