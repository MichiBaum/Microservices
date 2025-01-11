-- V1__Create_Chess_Service_Tables.sql
CREATE TABLE chess_engine (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(255) NOT NULL
);

CREATE TABLE game_move (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL,
    is_white BOOLEAN NOT NULL,
    move VARCHAR(255) NOT NULL,
    move_number INT NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

CREATE TABLE opening_move (
    id UUID PRIMARY KEY,
    move VARCHAR(255) NOT NULL,
    parent_id UUID,
    FOREIGN KEY (parent_id) REFERENCES opening_move(id) ON DELETE SET NULL
);

CREATE TABLE opening (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_move_id UUID NOT NULL,
    FOREIGN KEY (last_move_id) REFERENCES opening_move(id) ON DELETE CASCADE
);

CREATE TABLE game_move_evaluation (
    id UUID PRIMARY KEY,
    engine_id UUID NOT NULL,
    depth INT NOT NULL,
    evaluation FLOAT NOT NULL,
    game_move_id UUID NOT NULL,
    FOREIGN KEY (engine_id) REFERENCES chess_engine(id) ON DELETE CASCADE,
    FOREIGN KEY (game_move_id) REFERENCES game_move(id) ON DELETE CASCADE
);

CREATE TABLE opening_move_evaluation (
    id UUID PRIMARY KEY,
    engine_id UUID NOT NULL,
    depth INT NOT NULL,
    evaluation FLOAT NOT NULL,
    opening_move_id UUID NOT NULL,
    FOREIGN KEY (engine_id) REFERENCES chess_engine(id) ON DELETE CASCADE,
    FOREIGN KEY (opening_move_id) REFERENCES opening_move(id) ON DELETE CASCADE
);


-- Add indexes for performance optimization
CREATE INDEX idx_game_move_game_id ON game_move(game_id);