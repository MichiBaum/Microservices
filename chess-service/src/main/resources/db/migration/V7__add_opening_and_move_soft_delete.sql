ALTER TABLE opening ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE opening_move ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_opening_deleted ON opening(deleted);
CREATE INDEX idx_opening_move_deleted ON opening_move(deleted);
