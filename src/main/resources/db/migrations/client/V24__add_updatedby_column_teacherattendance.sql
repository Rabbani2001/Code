ALTER TABLE teacher_attendance
    ADD COLUMN updated_by VARCHAR(255) AFTER check_out_time;

ALTER TABLE teacher_attendance
    ADD COLUMN meta_data JSON AFTER updated_by;

ALTER TABLE staff_attendance
    ADD COLUMN updated_by VARCHAR(255) AFTER check_out_time;

ALTER TABLE staff_attendance
    ADD COLUMN meta_data JSON AFTER updated_by;

ALTER TABLE student_bus_fee_receipt MODIFY COLUMN session VARCHAR(255) NOT NULL;