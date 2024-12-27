-- Modify User and account table that user infos drop into separate account
alter table account modify platform enum ('CHESSCOM', 'LICHESS', 'OVER_THE_BOARD', 'FIDE') not null;
UPDATE account a SET a.platform = 'FIDE' WHERE a.platform = 'OVER_THE_BOARD';
alter table account modify platform enum ('CHESSCOM', 'LICHESS', 'FIDE') not null;

INSERT INTO account (id, created_at, person_id, name, platform_id, url, username, platform)
SELECT
    UUID() AS id,                                                   -- Generate a new UUID for the account ID
    NULL AS created_at,                                             -- Set created_at to NULL
    p.id AS person_id,                                              -- Use the person's ID
    CONCAT(p.lastname, ', ', p.firstname) AS name,                  -- Use the person's full name for the account name
    p.fide_id AS platform_id,                                       -- Use the person's FIDE ID as the platform_id
    CONCAT('https://ratings.fide.com/profile/', p.fide_id) AS url,  -- Generate the URL using FIDE ID
    CONCAT(p.lastname, ', ', p.firstname) AS username,              -- Use the person's full name for the account name
    'FIDE' AS platform                                              -- Set the platform to "FIDE"
FROM
    person p
WHERE
    p.fide_id IS NOT NULL AND p.fide_id != '' AND           -- Check that fide_id is not null and not empty
    NOT EXISTS (                                                    -- Check if the user has already a Fide account
        SELECT 1
        FROM account a
        WHERE a.person_id = p.id AND a.platform = 'FIDE'
    );

alter table account drop column url;
alter table person drop column fide_id;
alter table event add platform enum ('CHESSCOM', 'LICHESS', 'FIDE') NOT NULL DEFAULT 'FIDE';
alter table event MODIFY platform ENUM('CHESSCOM', 'LICHESS', 'FIDE') NOT NULL;
alter table event add internal_comment TEXT default '' not null;