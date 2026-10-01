CREATE TABLE visitor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visitor_id VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    phone_no VARCHAR(20),
    email VARCHAR(255),
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    resolution_status VARCHAR(100) NOT NULL CHECK (resolution_status IN ('pending', 'resolved')),
    address VARCHAR(255),
    visit_type VARCHAR(100) NOT NULL,
    visit_comments JSON NOT NULL,
    documents JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE supervisor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    employee_id VARCHAR(255) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    aadhar_no VARCHAR(255) UNIQUE,
    father_name VARCHAR(255),
    dob DATE,
    permanent_address VARCHAR(255),
    current_address VARCHAR(255),
    documents JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)