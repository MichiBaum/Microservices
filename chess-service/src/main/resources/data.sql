-- Events
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883d', '2024-11-21 00:00:00.000000', '2024-11-23 23:59:59.000000', 'https://www.chess.com/events/embed/2024-freestyle-chess-match-main-event', 'Freestyle Chess Match: Carlsen vs. Caruana', 'https://www.chess.com/events/info/2024-freestyle-chess-match-carlsen-caruana');
INSERT IGNORE INTO event (id, date_from, date_to, embed_url, title, url) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '2024-11-25 12:00:00.000000', '2024-12-13 23:59:59.000000', 'https://www.chess.com/events/embed/2024-fide-chess-world-championship', '2024 FIDE World Championship', 'https://www.chess.com/events/2024-fide-chess-world-championship');

-- Event Participations
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', 'eed8b0a2-d669-4dcf-beda-a2c51a76539d');
INSERT IGNORE INTO event_participants_mapping (event_id, person_id) VALUES ('557803ef-8079-4c57-81e0-d12492ab883f', '70509464-f3b9-4849-a010-d5207b284cc2');
