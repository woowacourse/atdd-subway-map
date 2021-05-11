TRUNCATE TABLE STATION;
ALTER TABLE STATION ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE LINE;
ALTER TABLE LINE ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE SECTION;
ALTER TABLE SECTION ALTER COLUMN id RESTART WITH 1;

-- station
INSERT INTO STATION (name) VALUES ('강남역');
INSERT INTO STATION (name) VALUES ('역삼역');
INSERT INTO STATION (name) VALUES ('아차산역');
INSERT INTO STATION (name) VALUES ('탄현역');
INSERT INTO STATION (name) VALUES ('일산역');
INSERT INTO STATION (name) VALUES ('홍대입구역');

-- line
INSERT INTO LINE (name, color) VALUES ('2호선', '초록색');
INSERT INTO LINE (name, color) VALUES ('경의중앙선', '하늘색');

-- section
INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (1, 1, 2, 10);
INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (2, 4, 5, 8);
INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (2, 5, 6, 10);