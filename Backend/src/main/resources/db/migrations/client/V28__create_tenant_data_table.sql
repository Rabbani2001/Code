CREATE TABLE tenant_data (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     sms_quota JSON NOT NULL,
     attendance_control JSON,
     tenant_info JSON,
     timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);