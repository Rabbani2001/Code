CREATE TABLE staff_attendance (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    status VARCHAR(255) CHECK (status IN ('present', 'absent', 'leave', 'holiday')),
    approved VARCHAR(255) CHECK (approved IN ('yes', 'no')),
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    timestamp TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS supervisor;

CREATE TABLE role_circular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(50) NOT NULL,
    role VARCHAR(100) NOT NULL,
    paid_leaves INT,
    sick_leaves INT,
    casual_leaves INT,
    basic_pay_percent INT,
    hra_percent INT,
    pf_percent INT,
    gratuity_percent INT,
    leave_rules JSON,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uk_role_circular_role_session UNIQUE (role, session)
);

CREATE TABLE employee_salary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(50) NOT NULL,
    ctc DOUBLE,
    basic_pay DOUBLE,
    hra DOUBLE,
    pf DOUBLE,
    bonus DOUBLE,
    gratuity DOUBLE,
    tax DOUBLE,
    deduction DOUBLE,
    pay_out JSON,
    lwp DOUBLE,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uk_employee_salary_username_session UNIQUE (username, session)
);

CREATE TABLE employee_leaves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    session VARCHAR(50) NOT NULL,
    paid_leaves DOUBLE,
    sick_leaves DOUBLE,
    casual_leaves DOUBLE,
    leaves_applied JSON,
    leaves_taken JSON,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT uk_employee_leaves_username_session UNIQUE (username, session)
);

DROP TABLE IF EXISTS admin;