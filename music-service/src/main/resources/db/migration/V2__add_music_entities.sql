CREATE TABLE artist (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    isni VARCHAR(16),
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL
);

CREATE TABLE artist_musixmatch (
    id UUID NOT NULL PRIMARY KEY,
    artist_id UUID NOT NULL,
    musixmatch_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_musixmatch_id UNIQUE (musixmatch_id),
    FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE artist_spotify (
    id UUID NOT NULL PRIMARY KEY,
    artist_id UUID NOT NULL,
    spotify_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_spotify_id UNIQUE (spotify_id),
    FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE album (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE album_musixmatch (
    id UUID NOT NULL PRIMARY KEY,
    album_id UUID NOT NULL,
    musixmatch_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_musixmatch_id UNIQUE (musixmatch_id),
    FOREIGN KEY (album_id) REFERENCES album(id)
);

CREATE TABLE album_spotify (
    id UUID NOT NULL PRIMARY KEY,
    album_id UUID NOT NULL,
    spotify_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_spotify_id UNIQUE (spotify_id),
    FOREIGN KEY (album_id) REFERENCES album(id)
);

CREATE TABLE track (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    release_date DATE,
    isrc VARCHAR(255) NOT NULL,
    CONSTRAINT uk_track_isrc UNIQUE (isrc)
);

CREATE TABLE isrc (
    track_id UUID NOT NULL,
    isrc VARCHAR(255) NOT NULL
);

CREATE TABLE track_musixmatch (
    id UUID NOT NULL PRIMARY KEY,
    track_id UUID NOT NULL,
    musixmatch_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_musixmatch_id UNIQUE (musixmatch_id),
    FOREIGN KEY (track_id) REFERENCES track(id)
);

CREATE TABLE track_spotify (
    id UUID NOT NULL PRIMARY KEY,
    track_id UUID NOT NULL,
    spotify_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_spotify_id UNIQUE (spotify_id),
    FOREIGN KEY (track_id) REFERENCES track(id)
);

CREATE TABLE track_artist_mapping (
    track_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (track_id, artist_id),
    CONSTRAINT fk_trackartistmapping_track FOREIGN KEY (track_id) REFERENCES track (id),
    CONSTRAINT fk_trackartistmapping_artist FOREIGN KEY (artist_id) REFERENCES artist (id)
);

CREATE TABLE album_artist_mapping (
    album_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (album_id, artist_id),
    CONSTRAINT fk_albumartistmapping_album FOREIGN KEY (album_id) REFERENCES album (id),
    CONSTRAINT fk_albumartistmapping_artist FOREIGN KEY (artist_id) REFERENCES artist (id)
);

CREATE TABLE spotify_account_activated (
    id UUID NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uk_user_id UNIQUE (user_id),
    CONSTRAINT uk_email UNIQUE (email)
);

CREATE TABLE record_label (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
);

CREATE TABLE record_label_artist_mapping (
    record_label_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (record_label_id, artist_id),
    CONSTRAINT fk_recordlabelartistmapping_record_label FOREIGN KEY (record_label_id) REFERENCES record_label (id),
    CONSTRAINT fk_recordlabelartistmapping_artist FOREIGN KEY (artist_id) REFERENCES artist (id)
);