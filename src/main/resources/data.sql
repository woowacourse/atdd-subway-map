INSERT INTO LINE (name, bg_color, start_time, end_time, interval_time)
VALUES ('5호선', 'bg-yellow-400 ', '02:00:00', '13:00:00', 10);
INSERT INTO STATION (name) VALUES ('삼성'), ('잠실'), ('석촌');
INSERT INTO LINE_STATION (line, line_key, station, pre_station_id, distance, duration)
VALUES (1, 0, 1, null, 1, 1), (1, 1, 2, 1, 2, 2);