INSERT INTO station (name) VALUES ('합정역');
INSERT INTO station (name) VALUES ('홍대입구역');
INSERT INTO station (name) VALUES ('신촌역');

INSERT INTO line (name, color) VALUES ('2호선', 'bg-green-600');

INSERT INTO section (line_id, up_station_id, down_station_id, distance)
VALUES ('1', '1', '2', '5');

INSERT INTO section (line_id, up_station_id, down_station_id, distance)
VALUES ('1', '2', '3', '5');

