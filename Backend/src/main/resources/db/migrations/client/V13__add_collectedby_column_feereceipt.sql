ALTER TABLE student_fee_receipt
ADD COLUMN collected_by VARCHAR(255) NOT NULL AFTER remarks;

ALTER TABLE student_bus_fee_receipt
ADD COLUMN collected_by VARCHAR(255) NOT NULL AFTER remarks;

CREATE TABLE student_fee_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(50) NOT NULL,
    tuition_fee DOUBLE,
    tuition_late_fee DOUBLE,
    tuition_concession DOUBLE,
    admission_fee DOUBLE,
    admission_fee_concession DOUBLE,
    registration_fee DOUBLE,
    registration_fee_concession DOUBLE,
    annual_fee DOUBLE,
    annual_fee_concession DOUBLE,
    other_fee_type JSON,
    timestamp TIMESTAMP NOT NULL
);

ALTER TABLE student_fee
RENAME COLUMN total_late_concession TO total_late_conc_schlr;

ALTER TABLE student_fee_receipt
RENAME COLUMN total_late_concession TO total_late_conc_schlr;

ALTER TABLE student_fee_receipt
ADD COLUMN scholarship BIGINT AFTER concession;