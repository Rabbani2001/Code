ALTER TABLE student_fee
MODIFY late_fee_charges DECIMAL(10,2),
MODIFY concession DECIMAL(10,2),
MODIFY scholarship DECIMAL(10,2),
MODIFY total DECIMAL(10,2),
MODIFY total_late_conc_schlr DECIMAL(10,2),
MODIFY outstanding DECIMAL(10,2);

-- Step 2: Drop old unique constraint (index)
ALTER TABLE student_fee
DROP INDEX uc_roll_class;

-- Step 3: Add new constraint with correct name
ALTER TABLE student_fee
ADD CONSTRAINT uc_username_session
UNIQUE (username, session);



-- Step 1: Convert BIGINT → DOUBLE
ALTER TABLE student_fee_receipt
MODIFY total DECIMAL(10,2) NOT NULL,
MODIFY total_late_conc_schlr DECIMAL(10,2) NOT NULL,
MODIFY late_fee_charges DECIMAL(10,2),
MODIFY concession DECIMAL(10,2),
MODIFY scholarship DECIMAL(10,2);

-- Step 2: Drop old unique constraint
ALTER TABLE student_fee_receipt
DROP INDEX uc_roll_class;

-- Step 3: Add new constraint with correct name
ALTER TABLE student_fee_receipt
ADD CONSTRAINT uc_username_session_schedules
UNIQUE (username, session, schedules);



-- ////////////////////////////////////////////////////


ALTER TABLE student_bus_fee
MODIFY late_fee_charges DECIMAL(10,2),
MODIFY concession DECIMAL(10,2),
MODIFY total DECIMAL(10,2),
MODIFY total_late_concession DECIMAL(10,2),
MODIFY outstanding DECIMAL(10,2);

-- Step 2: Drop old unique constraint (index)
ALTER TABLE student_bus_fee
DROP INDEX uc_roll_class;

-- Step 3: Add new constraint with correct name
ALTER TABLE student_bus_fee
ADD CONSTRAINT uc_username_session
UNIQUE (username, session);



-- Step 1: Convert BIGINT → DOUBLE
ALTER TABLE student_bus_fee_receipt
MODIFY total DECIMAL(10,2),
MODIFY total_late_concession DECIMAL(10,2) NOT NULL,
MODIFY late_fee_charges DECIMAL(10,2),
MODIFY concession DECIMAL(10,2);

-- Step 2: Drop old unique constraint
ALTER TABLE student_bus_fee_receipt
DROP INDEX uc_roll_class;

-- Step 3: Add new constraint with correct name
ALTER TABLE student_bus_fee_receipt
ADD CONSTRAINT uc_username_session_schedules
UNIQUE (username, session, schedules);

