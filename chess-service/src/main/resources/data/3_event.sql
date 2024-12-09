-- FIDE World Championship
INSERT IGNORE INTO event_category (id, description, name) VALUES ('860742ff-6cc4-415f-be27-67373962781d', 'FIDE World Championship', 'FIDE World Championship');

-- FIDE World Championship 2006
-- FIDE World Championship 2007
-- FIDE World Championship 2008
-- FIDE World Championship 2010
-- FIDE World Championship 2012
-- FIDE World Championship 2013
-- FIDE World Championship 2014
-- FIDE World Championship 2016

-- FIDE World Championship 2018
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, location, url) VALUES ('2689e819-6e1f-4aaa-8692-6bb9ed9a31d4', '2018-11-09', '2018-11-30', 'https://www.chess.com/events/embed/2018-wcc', '2018 FIDE World Championship', 'London', 'https://www.chess.com/events/2018-wcc');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('2689e819-6e1f-4aaa-8692-6bb9ed9a31d4', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('2689e819-6e1f-4aaa-8692-6bb9ed9a31d4', '74dad1b1-2f90-45b4-8b1a-c1d47f2296ee');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('2689e819-6e1f-4aaa-8692-6bb9ed9a31d4', '4008d5b0-1540-4e0c-82bf-de37b4f759c3');

-- FIDE World Championship 2021
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, location, url) VALUES ('e4c203c8-f064-4a5f-a9af-8e4af0796921', '2021-11-24', '2021-12-14', 'https://www.chess.com/events/embed/2021-fide-world-chess-championship', '2021 FIDE World Championship', 'Dubai', 'https://www.chess.com/events/2021-fide-world-chess-championship');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('e4c203c8-f064-4a5f-a9af-8e4af0796921', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('e4c203c8-f064-4a5f-a9af-8e4af0796921', '4008d5b0-1540-4e0c-82bf-de37b4f759c3');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('e4c203c8-f064-4a5f-a9af-8e4af0796921', '93563420-186e-3439-9d13-837a9e604392');

-- FIDE World Championship 2023
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, location, url) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '2023-04-09', '2023-05-01', 'https://www.chess.com/events/embed/2023-fide-world-chess-championship', '2023 FIDE World Championship ', 'Astana', 'https://www.chess.com/events/2023-fide-world-chess-championship');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '93563420-186e-3439-9d13-837a9e604392');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '70509464-f3b9-4849-a010-d5207b284cc2');

-- FIDE World Championship 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, location, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '2024-11-25', '2024-12-13', 'https://www.chess.com/events/embed/2024-fide-chess-world-championship', '2024 FIDE World Championship', 'Singapore', 'https://www.chess.com/events/2024-fide-chess-world-championship');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', 'eed8b0a2-d669-4dcf-beda-a2c51a76539d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '70509464-f3b9-4849-a010-d5207b284cc2');


-- Freestyle Chess Tour
INSERT IGNORE INTO event_category (id, description, name) VALUES ('860742ff-6cc4-415f-be27-67373972781d', 'Freestyle Chess Tour', 'Freestyle Chess Tour');

INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883d', '2024-11-21', '2024-11-22', 'https://www.chess.com/events/embed/2024-freestyle-chess-match-main-event', 'Carlsen vs. Caruana', 'https://www.chess.com/events/info/2024-freestyle-chess-match-carlsen-caruana');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883d', '860742ff-6cc4-415f-be27-67373972781d');

INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ac883d', '2025-02-07', '2025-02-14', '', 'Freestyle Chess', '');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('557803ef-8079-4c57-81e0-d12492ac883d', '860742ff-6cc4-415f-be27-67373972781d');

-- Freestyle Chess Tour
INSERT IGNORE INTO event_category (id, description, name) VALUES ('a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935', 'Champions Chess Tour', 'Champions Chess Tour');

-- Champions Chess Tour Chessable Masters 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('ab91d518-b423-450b-be08-6ab21bffe172', '2024-01-31', '2024-02-07', 'https://www.chess.com/events/embed/2024-champions-chess-tour-chessable-masters-division-1', 'Chessable Masters 2024', 'https://www.chess.com/events/info/2024-champions-chess-tour-chessable-masters');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('ab91d518-b423-450b-be08-6ab21bffe172', 'a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935');

-- Champions Chess Tour Chess.com Classic 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('1cdad317-b444-47ca-8d2a-6df837ff2b56', '2024-05-08', '2024-05-15', 'https://www.chess.com/events/embed/2024-champions-chess-tour-chesscom-classic-division-1', 'Chess.com Classic 2024', 'https://www.chess.com/events/info/2024-champions-chess-tour-chesscom-classic');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('1cdad317-b444-47ca-8d2a-6df837ff2b56', 'a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935');

-- Champions Chess Tour CrunchLabs Masters 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('059fbe8d-4b19-4988-8c8d-460b2b1ad37b', '2024-07-17', '2024-07-24', 'https://www.chess.com/events/embed/2024-champions-chess-tour-crunchlabs-masters-division-1', 'CrunchLabs Masters 2024', 'https://www.chess.com/events/info/2024-champions-chess-tour-crunchlabs-masters');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('059fbe8d-4b19-4988-8c8d-460b2b1ad37b', 'a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935');

-- Champions Chess Tour Julius Baer Generation Cup 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('b5432d3f-ca5e-4703-8aa8-895878292dbd', '2024-09-25', '2024-10-01', 'https://www.chess.com/events/embed/2024-champions-chess-tour-julius-baer-generation-cup-division-1', 'Julius Baer Generation Cup 2024', 'https://www.chess.com/events/info/2024-champions-chess-tour-julius-baer-generation-cup');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('b5432d3f-ca5e-4703-8aa8-895878292dbd', 'a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935');

-- Champions Chess Tour Finals 2024
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('583fa28e-22e0-42a4-84cf-b00eab1a3642', '2024-12-17', '2024-12-21', 'https://www.chess.com/events/embed/2024-champions-chess-tour-finals-knockout-stage', 'Finals 2024', 'https://www.chess.com/events/2024-champions-chess-tour-finals-knockout-stage');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('583fa28e-22e0-42a4-84cf-b00eab1a3642', 'a9c6b26d-eda2-40d7-b6e5-c8a3beb4f935');

