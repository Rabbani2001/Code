ALTER TABLE student_attendance
DROP CHECK student_attendance_chk_1;

ALTER TABLE student_attendance
ADD CONSTRAINT student_attendance_chk_1
CHECK (status IN ('present', 'absent', 'leave', 'holiday'));

ALTER TABLE student_attendance
DROP CHECK student_attendance_chk_2;

ALTER TABLE student_attendance
ADD CONSTRAINT student_attendance_chk_2
CHECK (approved IN ('yes', 'no'));



ALTER TABLE teacher_attendance
DROP CHECK teacher_attendance_chk_1;

ALTER TABLE teacher_attendance
ADD CONSTRAINT teacher_attendance_chk_1
CHECK (status IN ('present', 'absent', 'leave', 'holiday'));

ALTER TABLE teacher_attendance
DROP CHECK teacher_attendance_chk_2;

ALTER TABLE teacher_attendance
ADD CONSTRAINT teacher_attendance_chk_2
CHECK (approved IN ('yes', 'no'));



ALTER TABLE student_fee_receipt
DROP CHECK student_fee_receipt_chk_1;

ALTER TABLE student_fee_receipt
ADD CONSTRAINT student_fee_receipt_chk_1
CHECK (mode IN ('cash', 'online', 'bank'));

ALTER TABLE student_bus_fee_receipt
DROP CHECK student_bus_fee_receipt_chk_1;

ALTER TABLE student_bus_fee_receipt
ADD CONSTRAINT student_bus_fee_receipt_chk_1
CHECK (mode IN ('cash', 'online', 'bank'));