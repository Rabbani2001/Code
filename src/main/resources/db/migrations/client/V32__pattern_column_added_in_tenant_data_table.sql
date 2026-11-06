ALTER TABLE tenant_data
    MODIFY COLUMN sms_quota JSON NULL,
    ADD COLUMN pattern JSON NULL AFTER tenant_info;