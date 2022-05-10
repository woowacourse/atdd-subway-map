INSERT INTO station(id, name)
VALUES (1, '이미 존재하는 역 이름'),
       (2, '선릉역'),
       (3, '잠실역');

INSERT INTO line(id, name, color)
VALUES (1, '이미 존재하는 노선 이름', '노란색'),
       (2, '신분당선', '빨간색'),
       (3, '2호선', '초록색');

INSERT INTO section(id, line_id, up_station_id, down_station_id, distance)
VALUES (1, 1, 1, 2, 5),
       (2, 1, 2, 3, 5),
       (3, 1, 1, 3, 7);
