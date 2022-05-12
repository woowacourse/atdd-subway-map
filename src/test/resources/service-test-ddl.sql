INSERT INTO station(id, name)
VALUES (1, '복정역'),
       (2, '가천대역'),
       (3, '태평역'),
       (4, '잠실역'),
       (5, '잠실나루역'),
       (6, '강변역'),
       (7, '모란역'),
       (8, '수서역');

INSERT INTO line(id, name, color)
VALUES (1, '분당선', '노란색'),
       (2, '2호선', '초록색');

INSERT INTO section(id, line_id, up_station_id, down_station_id, distance)
VALUES (1, 1, 1, 2, 10),
       (2, 1, 2, 3, 5),
       (3, 2, 4, 5, 10),
       (4, 2, 5, 6, 2);
