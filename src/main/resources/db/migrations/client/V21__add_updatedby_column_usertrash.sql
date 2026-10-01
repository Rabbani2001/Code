ALTER TABLE user_trash
ADD COLUMN updated_by VARCHAR(255) AFTER role;

ALTER TABLE teacher DROP COLUMN updated_by;
ALTER TABLE staff DROP COLUMN updated_by;
ALTER TABLE parent DROP COLUMN updated_by;


