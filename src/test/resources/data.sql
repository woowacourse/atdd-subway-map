INSERT INTO STATION(name)
VALUES ('아마역');

INSERT INTO STATION(name)
VALUES ('마찌역');

INSERT INTO STATION(name)
VALUES ('잠실역');

INSERT INTO STATION(name)
VALUES ('강남역');

INSERT INTO STATION(name)
VALUES ('삼전역');

INSERT INTO LINE(name, color)
VALUES ('9호선', 'bg-red-600');

INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance)
VALUES (1L, 1L, 2L, 10);
