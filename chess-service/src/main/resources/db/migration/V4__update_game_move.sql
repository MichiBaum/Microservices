-- Step 1: Add the `account_id` column to the `player` table
ALTER TABLE player
    ADD COLUMN account_id UUID;

-- Step 2: Update the `player` table with the corresponding `account_id`
-- This maps `player` records to the correct `account` using `platform_id`
UPDATE player p
    JOIN account a
    ON p.platform_id = a.platform_id
        AND p.username = a.username -- Match both platform_id and username for accuracy
SET p.account_id = a.id;

-- Step 4: Drop the `account_game_mapping` table (no longer needed)
DROP TABLE account_game_mapping;

-- Step 5: Update foreign key constraints
-- Add foreign key from `player.account_id` to `account.id`
ALTER TABLE player
    ADD CONSTRAINT fk_player_account
        FOREIGN KEY (account_id)
            REFERENCES account(id);

-- Ensure the existing foreign key to `game` is properly defined
ALTER TABLE player
    ADD CONSTRAINT fk_player_game
        FOREIGN KEY (game_id)
            REFERENCES game(id);

-- Step 6: Clean up
ALTER TABLE player DROP COLUMN platform_id;
alter table account drop foreign key FKd9dhia7smrg88vcbiykhofxee;
alter table account add constraint person_id foreign key (person_id) references person (id);
create index person_id_index on account (person_id);
drop index IF EXISTS FKd9dhia7smrg88vcbiykhofxee on account;
alter table player drop foreign key FK8095bt0vv5capccv9870ln2n;
create index game_id_index on player (game_id);
drop index IF EXISTS FK8095bt0vv5capccv9870ln2n on player;
alter table game add constraint event_id foreign key (event_id) references event (id);
alter table game drop foreign key FKnqufihgcswqe5fvhkfpuj7201;
create index event_id on game (event_id);
drop index IF EXISTS FKnqufihgcswqe5fvhkfpuj7201 on game;










