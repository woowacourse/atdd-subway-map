INSERT INTO LINE (name, start_time, end_time, interval_time, created_at, updated_at, color)
VALUES ('공항철도', '05:30:00', '23:30:00', 20, NOW(), NOW(), 'bg-blue-500');

INSERT INTO LINE (name, start_time, end_time, interval_time, created_at, updated_at, color)
VALUES ('1호선', '05:40:00', '23:30:00', 10, NOW(), NOW(), 'bg-blue-700');

INSERT INTO STATION (name, created_at)
VALUES ('검암역', NOW());

INSERT INTO STATION (name, created_at)
VALUES ('계양역', NOW());

INSERT INTO STATION (name, created_at)
VALUES ('김포공항역', NOW());

INSERT INTO STATION (name, created_at)
VALUES ('마곡나루역', NOW());

INSERT INTO STATION (name, created_at)
VALUES ('서울역', NOW());

INSERT INTO STATION (name, created_at)
VALUES ('용산역', NOW());

INSERT INTO EDGE (line_id, line_key, station_id, pre_station_id, distance, duration)
VALUES (1, 1, 1, null, 10, 10);
INSERT INTO EDGE (line_id, line_key, station_id, pre_station_id, distance, duration)
VALUES (1, 2, 2, 1, 10, 10);
INSERT INTO EDGE (line_id, line_key, station_id, pre_station_id, distance, duration)
VALUES (1, 3, 3, 2, 10, 10);
INSERT INTO EDGE (line_id, line_key, station_id, pre_station_id, distance, duration)
VALUES (1, 4, 4, 3, 10, 10);