ALTER TABLE student_fee_receipt
ADD COLUMN concession_split JSON AFTER concession;


CREATE TABLE student_combine_fee_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(50) NOT NULL,
    tuition_fee DOUBLE,
    tuition_scholarship DOUBLE,
    admission_fee DOUBLE,
    registration_fee DOUBLE,
    annual_fee DOUBLE,
    concession DOUBLE,
    other_fee_type JSON,
    timestamp TIMESTAMP NOT NULL
);
