CREATE TABLE popular_opening (
    id UUID PRIMARY KEY,
    opening_id UUID NOT NULL,
    ranking INT NOT NULL,
    CONSTRAINT fk_opening FOREIGN KEY (opening_id) REFERENCES opening (id)
);
