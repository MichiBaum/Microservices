CREATE TABLE user_library_artist (
    id UUID NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    artist_id UUID NOT NULL,
    created DATETIME(6) NOT NULL,
    CONSTRAINT uk_user_artist UNIQUE (user_id, artist_id),
    CONSTRAINT fk_userlibraryartist_artist FOREIGN KEY (artist_id) REFERENCES artist (id)
);
