ALTER TABLE teacher
ADD COLUMN subject_classes JSON AFTER class_names;

ALTER TABLE teacher
ADD COLUMN updated_by VARCHAR(255) AFTER documents;

ALTER TABLE class_circular
ADD COLUMN test_schedules JSON AFTER exam_schedules;

ALTER TABLE student_exam
ADD COLUMN test_schedules JSON AFTER exam_schedules;

ALTER TABLE student_exam
ADD COLUMN test_schedule_comments JSON AFTER exam_schedule_comments;


CREATE TABLE user_trash (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone_no VARCHAR(20) NOT NULL UNIQUE,
    alt_phone_no VARCHAR(20),
    tenant_id VARCHAR(255)  NOT NULL,
    role VARCHAR(255)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

