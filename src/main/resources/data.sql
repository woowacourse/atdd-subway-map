
INSERT INTO LINE (name, start_time, end_time, interval_time, created_at, updated_at, color)
VALUES ('공항철도', '05:30:00', '23:30:00', 20, NOW(), NOW(), 'bg-blue-500');
INSERT INTO LINE (name, start_time, end_time, interval_time, created_at, updated_at, color)
VALUES ('1호선', '05:40:00', '23:30:00', 10, NOW(), NOW(), 'bg-blue-700');

INSERT INTO STATION (name, created_at) VALUES ('공릉역', NOW());
INSERT INTO STATION (name, created_at) VALUES ('하계역', NOW());
INSERT INTO STATION (name, created_at) VALUES ('그니역', NOW());

INSERT INTO EDGE (line, station_id, pre_station_id, distance, duration, sequence)
VALUES (1, 2, 1, 0, 0, 1);
INSERT INTO EDGE (line, station_id, pre_station_id, distance, duration, sequence)
VALUES (1, 1, 1, 0, 0, 0);