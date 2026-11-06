CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone_no VARCHAR(20) NOT NULL UNIQUE,
    alt_phone_no VARCHAR(20),
    tenant_id VARCHAR(255)  NOT NULL,
    role VARCHAR(255)  NOT NULL,
    is_active BOOLEAN  NOT NULL,
    password VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE parent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    parent_type VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    aadhar_no VARCHAR(255) UNIQUE,
    pan_no VARCHAR(255) UNIQUE,
    gender VARCHAR(255),
    dob DATE,
    documents JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- Create the teacher table
CREATE TABLE teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    employee_id VARCHAR(255) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    aadhar_no VARCHAR(255) UNIQUE,
    pan_no VARCHAR(255) UNIQUE,
    class_names JSON,
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    guardian_name VARCHAR(255),
    qualification VARCHAR(100),
    designation VARCHAR(50) NOT NULL,
    dob DATE,
    permanent_address VARCHAR(255),
    current_address VARCHAR(255),
    joining_date DATE,
    bus_route VARCHAR(255),
    documents JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create the admin table
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    employee_id VARCHAR(255) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    aadhar_no VARCHAR(255) UNIQUE,
    pan_no VARCHAR(255) UNIQUE,
    class_names JSON,
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    guardian_name VARCHAR(255),
    qualification VARCHAR(100),
    designation VARCHAR(50) NOT NULL,
    dob DATE,
    permanent_address VARCHAR(255),
    current_address VARCHAR(255),
    joining_date DATE,
    bus_route VARCHAR(255),
    documents JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    parent_username VARCHAR(255) NOT NULL,
    parent_type VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    admission_no VARCHAR(255) UNIQUE,
    admission_date DATE,
    aadhar_no VARCHAR(255) UNIQUE,
    apaar_no VARCHAR(255) UNIQUE,
    gender VARCHAR(255),
    class_name VARCHAR(255),
    roll_no BIGINT,
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    guardian_name VARCHAR(255),
    dob DATE,
    medium VARCHAR(255),
    permanent_address VARCHAR(255),
    current_address VARCHAR(255),
    rural_or_urban VARCHAR(20),
    bus_route VARCHAR(255),
    documents JSON,
    parent_actions JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_roll_class UNIQUE (roll_no, class_name)
);

-- Create the student_other_info table
CREATE TABLE student_other_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    family_id VARCHAR(255),
    father_dob DATE,
    father_aadhar_no VARCHAR(255),
    father_occupation VARCHAR(255),
    father_education VARCHAR(255),
    mother_dob DATE,
    mother_aadhar_no VARCHAR(255),
    mother_occupation VARCHAR(255),
    mother_education VARCHAR(255),
    guardian_dob DATE,
    guardian_aadhar_no VARCHAR(255),
    guardian_occupation VARCHAR(255),
    guardian_education VARCHAR(255),
    guardian_gender VARCHAR(255),
    religion VARCHAR(255),
    caste VARCHAR(255),
    nationality VARCHAR(255),
    domicile VARCHAR(255),
    is_minority VARCHAR(10),
    minority_category VARCHAR(255),
    is_bpl VARCHAR(10),
    stream VARCHAR(255),
    subjects_opted VARCHAR(255),
    subjects_optional VARCHAR(255),
    previous_school_name VARCHAR(255),
    previous_admission_no VARCHAR(255),
    previous_school_code VARCHAR(255),
    previous_leaving_date VARCHAR(50),
    previous_class_passed VARCHAR(255),
    previous_marks_obtained VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);




CREATE TABLE student_attendance (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    class_name VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('PRESENT', 'ABSENT', 'LEAVE', 'HOLIDAY')),
    approved VARCHAR(255) CHECK (approved IN ('YES', 'NO')),
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE teacher_attendance (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    status VARCHAR(255) CHECK (status IN ('PRESENT', 'ABSENT', 'LEAVE', 'HOLIDAY')),
    approved VARCHAR(255) CHECK (approved IN ('YES', 'NO')),
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    timestamp TIMESTAMP NOT NULL
);


CREATE TABLE class_circular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(255) NOT NULL,
    class_name VARCHAR(255) NOT NULL,
    school_monthly_fees JSON,
    school_misc_fees JSON,
    school_fees_rules JSON,
    subjects JSON,
    exam_schedules JSON,
    class_qualities JSON,
    grade_circular JSON,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uc_roll_class UNIQUE (class_name, session)
);


CREATE TABLE holiday_circular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    holiday_name VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE bus_circular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(255) NOT NULL,
    bus_route VARCHAR(255) NOT NULL,
    bus_monthly_fees JSON,
    bus_fees_rules JSON,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uc_roll_class UNIQUE (bus_route, session)
);

CREATE TABLE student_fee (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(255) NOT NULL,
    school_monthly_fees JSON,
    school_misc_fees JSON,
    late_fee_charges BIGINT,
    concession BIGINT,
    total BIGINT,
    total_late_concession BIGINT,
    outstanding BIGINT,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uc_roll_class UNIQUE (username, session)
);


CREATE TABLE student_fee_receipt (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(255) NOT NULL,
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
    CONSTRAINT uc_roll_class UNIQUE (username, session, schedules)
);



CREATE TABLE bank_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    bank_name VARCHAR(150) NOT NULL,
    bank_account_no VARCHAR(50) NOT NULL UNIQUE,
    account_holder_name VARCHAR(150) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    upi_id VARCHAR(100) UNIQUE,
    timestamp TIMESTAMP NOT NULL
);


