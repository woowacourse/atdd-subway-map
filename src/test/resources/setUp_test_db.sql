INSERT INTO station (name) VALUES ('강남역');
INSERT INTO station (name) VALUES ('역삼역');
INSERT INTO station (name) VALUES ('선릉역');

-- line
INSERT INTO line (name, color) VALUES ('2호선', 'rg-red-600');

-- section
INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (1, 1, 2, 10);
