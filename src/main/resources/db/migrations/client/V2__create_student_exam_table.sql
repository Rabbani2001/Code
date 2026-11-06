CREATE TABLE student_exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    result_id VARCHAR(255) NOT NULL UNIQUE,
    class_name VARCHAR(255),
    exam_schedules JSON,
    exam_schedule_comments JSON,
    exam_remarks VARCHAR(255),
    class_qualities JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_roll_class UNIQUE (username, session)
);

