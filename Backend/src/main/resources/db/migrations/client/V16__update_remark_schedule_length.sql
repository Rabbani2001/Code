-- Step 1: Drop existing constraint
ALTER TABLE student_fee_receipt
DROP INDEX uc_username_session_schedules;
-- Step 2: Modify columns
ALTER TABLE student_fee_receipt
MODIFY remarks VARCHAR(100),
MODIFY schedules VARCHAR(1000) NOT NULL;


-- Step 1: Drop existing constraint
ALTER TABLE student_bus_fee_receipt
DROP INDEX uc_username_session_schedules;
-- Step 2: Modify columns
ALTER TABLE student_bus_fee_receipt
MODIFY remarks VARCHAR(100),
MODIFY schedules VARCHAR(1000) NOT NULL;
