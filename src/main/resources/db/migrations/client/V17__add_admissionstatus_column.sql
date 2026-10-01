ALTER TABLE student
ADD COLUMN admission_status VARCHAR(10) NOT NULL DEFAULT 'OLD'
CHECK (admission_status IN ('OLD', 'NEW'))
AFTER admission_date;
