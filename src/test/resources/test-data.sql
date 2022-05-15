INSERT INTO station (name) values('삼전역');
INSERT INTO station (name) values('잠실역');
INSERT INTO station (name) values('석촌역');

INSERT INTO line (name, color) values('9호선', '갈색');
INSERT INTO line (name, color) values('신분당선', '빨간색');
INSERT INTO line (name, color) values('경의중앙선', '민트색');

INSERT INTO sections (line_id, up_station_id, down_station_id, distance) values(1,1,2,10);
INSERT INTO sections (line_id, up_station_id, down_station_id, distance) values(2,2,3,10);
INSERT INTO sections (line_id, up_station_id, down_station_id, distance) values(3,1,3,10);
