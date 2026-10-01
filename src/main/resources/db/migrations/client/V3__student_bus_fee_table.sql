CREATE TABLE student_bus_fee (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(255) NOT NULL,
    bus_monthly_fees JSON,
    late_fee_charges BIGINT,
    concession BIGINT,
    total BIGINT,
    total_late_concession BIGINT,
    outstanding BIGINT,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uc_roll_class UNIQUE (username, session)
);

-- Create new table with updated structure
CREATE TABLE student_bus_fee_receipt (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(255),
    receipt_no VARCHAR(255) NOT NULL UNIQUE,
    date DATE NOT NULL,
    schedules VARCHAR(255) NOT NULL,
    mode VARCHAR(255) NOT NULL CHECK (mode IN ('CASH', 'ONLINE', 'BANK')),
    total BIGINT NOT NULL,
    total_late_concession BIGINT NOT NULL,
    late_fee_charges BIGINT,
    concession BIGINT,
    remarks VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uc_roll_class UNIQUE (username, session,schedules)
);
