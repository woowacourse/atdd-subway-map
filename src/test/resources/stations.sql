TRUNCATE TABLE Station;
TRUNCATE TABLE Section;
TRUNCATE TABLE Line;

INSERT INTO STATION (name) VALUES ('강남역');
INSERT INTO STATION (name) VALUES ('역삼역');
INSERT INTO STATION (name) VALUES ('삼성역');
INSERT INTO STATION (name) VALUES ('선릉역');
INSERT INTO STATION (name) VALUES ('잠실역');
INSERT INTO LINE (name, color) VALUES ('2호선', 'green');
INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (1, 1, 3, 10);