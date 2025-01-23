-- V6__change_evaluation_to_string.sql
-- Step 1: Alter the column type
ALTER TABLE opening_move_evaluation
    MODIFY evaluation VARCHAR(255) NOT NULL;

-- Optional: If applicable, convert existing FLOAT values to formatted strings
-- Note: Adjust the format as per your use case
UPDATE opening_move_evaluation
SET evaluation = CAST(evaluation AS CHAR);