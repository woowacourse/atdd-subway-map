INSERT INTO LINE (name, color) VALUES ('2호선', '초록색');

INSERT INTO STATION (name) VALUES ('지하철역1');
INSERT INTO STATION (name) VALUES ('지하철역2');
INSERT INTO STATION (name) VALUES ('지하철역3');
INSERT INTO STATION (name) VALUES ('지하철역4');

INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (1, 1, 2, 10);
