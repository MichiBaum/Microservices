-- Add columns to game table
ALTER TABLE game ADD COLUMN source_type ENUM('ONLINE_PLATFORM', 'OTB', 'IMPORTED') NULL;
ALTER TABLE game ADD COLUMN platform ENUM('CHESSCOM', 'LICHESS', 'FIDE', 'FREESTYLE') NULL;
ALTER TABLE game ADD COLUMN external_game_id VARCHAR(255) NULL;
ALTER TABLE game ADD COLUMN game_result ENUM('WHITE_WIN', 'BLACK_WIN', 'DRAW', 'ONGOING', 'NOT_STARTED') NULL;
ALTER TABLE game ADD COLUMN termination ENUM('CHECKMATE', 'RESIGNATION', 'TIMEOUT', 'STALEMATE', 'DRAW_AGREEMENT', 'REPETITION', 'FIFTY_MOVE_RULE', 'INSUFFICIENT_MATERIAL', 'ABANDONMENT', 'TIME_EXPIRED_VS_INSUFFICIENT_MATERIAL') NULL;
ALTER TABLE game ADD COLUMN played_at DATETIME NULL;
ALTER TABLE game ADD COLUMN time_control VARCHAR(255) NULL;
ALTER TABLE game ADD COLUMN time_control_category ENUM('BULLET', 'BLITZ', 'RAPID', 'CLASSICAL', 'CORRESPONDENCE') NULL;
ALTER TABLE game ADD COLUMN variant ENUM('STANDARD', 'CHESS960', 'KING_OF_THE_HILL', 'THREE_CHECK', 'CRAZYHOUSE', 'ATOMIC', 'HORDE', 'RACING_KINGS') NOT NULL DEFAULT 'STANDARD';
ALTER TABLE game ADD COLUMN white_player_id UUID NULL;
ALTER TABLE game ADD COLUMN black_player_id UUID NULL;

-- Migrate data in game table
UPDATE game SET source_type = 'ONLINE_PLATFORM' WHERE chess_platform IN ('CHESSCOM', 'LICHESS');
UPDATE game SET source_type = 'OTB' WHERE chess_platform = 'OVER_THE_BOARD';
UPDATE game SET platform = 'CHESSCOM' WHERE chess_platform = 'CHESSCOM';
UPDATE game SET platform = 'LICHESS' WHERE chess_platform = 'LICHESS';
UPDATE game SET external_game_id = platform_id;
UPDATE game SET game_result = 'ONGOING' WHERE game_result IS NULL;
UPDATE game SET played_at = NOW() WHERE played_at IS NULL;
UPDATE game SET source_type = 'IMPORTED' WHERE source_type IS NULL;

-- Make mandatory columns NOT NULL
ALTER TABLE game MODIFY source_type ENUM('ONLINE_PLATFORM', 'OTB', 'IMPORTED') NOT NULL;
ALTER TABLE game MODIFY game_result ENUM('WHITE_WIN', 'BLACK_WIN', 'DRAW', 'ONGOING', 'NOT_STARTED') NOT NULL;
ALTER TABLE game MODIFY played_at DATETIME NOT NULL;

-- Link white and black players in game table
UPDATE game g SET g.white_player_id = (SELECT p.id FROM player p WHERE p.game_id = g.id AND p.piece_color = 'WHITE' LIMIT 1);
UPDATE game g SET g.black_player_id = (SELECT p.id FROM player p WHERE p.game_id = g.id AND p.piece_color = 'BLACK' LIMIT 1);

-- Modify player table
ALTER TABLE player MODIFY username VARCHAR(255) NOT NULL;
ALTER TABLE player MODIFY rating BIGINT NULL;
ALTER TABLE player MODIFY piece_color ENUM('BLACK', 'WHITE') NOT NULL;

-- Drop obsolete columns from game
ALTER TABLE game DROP COLUMN chess_platform;
ALTER TABLE game DROP COLUMN platform_id;
ALTER TABLE game DROP COLUMN game_type;

ALTER TABLE player DROP FOREIGN KEY fk_player_game;
ALTER TABLE player DROP COLUMN game_id;

-- Add new foreign keys for game table
ALTER TABLE game ADD CONSTRAINT fk_game_white_player FOREIGN KEY (white_player_id) REFERENCES player (id);
ALTER TABLE game ADD CONSTRAINT fk_game_black_player FOREIGN KEY (black_player_id) REFERENCES player (id);