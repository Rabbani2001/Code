ALTER TABLE student
ADD COLUMN blood_group VARCHAR(255) AFTER gender;

ALTER TABLE student
ADD COLUMN updated_by VARCHAR(255) AFTER documents;

ALTER TABLE user
ADD COLUMN updated_by VARCHAR(255) AFTER role;

ALTER TABLE user
ADD COLUMN last_login TIMESTAMP AFTER tenant_id;

ALTER TABLE parent
ADD COLUMN updated_by VARCHAR(255) AFTER documents;

ALTER TABLE staff
ADD COLUMN updated_by VARCHAR(255) AFTER documents;

ALTER TABLE employee_salary
ADD COLUMN arrear DOUBLE AFTER tax;





