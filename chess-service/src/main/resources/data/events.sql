INSERT IGNORE INTO event_category (id, description, name) VALUES ('860742ff-6cc4-415f-be27-67373962781d', 'FIDE World Championship', 'FIDE World Championship');


INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883d', '2024-11-21', '2024-11-23', 'https://www.chess.com/events/embed/2024-freestyle-chess-match-main-event', 'Freestyle Chess Match: Carlsen vs. Caruana', 'https://www.chess.com/events/info/2024-freestyle-chess-match-carlsen-caruana');


INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '2024-11-25', '2024-12-13', 'https://www.chess.com/events/embed/2024-fide-chess-world-championship', '2024 FIDE World Championship', 'https://www.chess.com/events/2024-fide-chess-world-championship');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', 'eed8b0a2-d669-4dcf-beda-a2c51a76539d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '70509464-f3b9-4849-a010-d5207b284cc2');

INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '2023-04-09', '2023-05-01', 'https://www.chess.com/events/embed/2023-fide-world-chess-championship', 'FIDE World Chess Championship 2023', 'https://www.chess.com/events/2023-fide-world-chess-championship');
INSERT IGNORE INTO event_category_mapping (event_id, category_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '860742ff-6cc4-415f-be27-67373962781d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '93563420-186E-3439-9D13-837A9E604392');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('7f7fe0c3-c529-4315-8507-8068173fbb1f', '70509464-f3b9-4849-a010-d5207b284cc2');