ALTER TABLE class_circular
    ADD COLUMN exam_schedules_grade JSON NULL AFTER exam_schedules;

ALTER TABLE class_circular
    ADD COLUMN test_schedules_grade JSON NULL AFTER test_schedules;

ALTER TABLE student_exam
    ADD COLUMN exam_schedules_grade JSON NULL AFTER exam_schedules;

ALTER TABLE student_exam
    ADD COLUMN test_schedules_grade JSON NULL AFTER test_schedules;