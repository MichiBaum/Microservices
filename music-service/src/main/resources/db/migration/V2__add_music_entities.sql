CREATE TABLE artist (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE album (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE track (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    release_date DATE,
    isrc VARCHAR(255) NOT NULL UNIQUE,
    ean VARCHAR(255),
    upc VARCHAR(255)
);

CREATE TABLE track_artist_mapping (
    track_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (track_id, artist_id),
    FOREIGN KEY (track_id) REFERENCES track (id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist (id) ON DELETE CASCADE
);

CREATE TABLE album_artist_mapping (
    album_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (album_id, artist_id),
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist (id) ON DELETE CASCADE
);
