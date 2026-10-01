ALTER TABLE student_fee
ADD COLUMN scholarship BIGINT AFTER concession;

ALTER TABLE staff
ADD COLUMN role VARCHAR(255) NOT NULL AFTER username;
