INSERT INTO station(name) VALUES ('강남역');
INSERT INTO station(name) VALUES ('역삼역');
INSERT INTO station(name) VALUES ('잠실역');
INSERT INTO station(name) VALUES ('강변역');
INSERT INTO station(name) VALUES ('뚝섬역');
INSERT INTO station(name) VALUES ('성수역');


INSERT INTO line(name, color) VALUES ('1호선', 'black');

INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES ('1', '1', '2', '3');
INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES ('1', '2', '3', '3');
INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES ('1', '3', '4', '3');
INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES ('1', '4', '5', '3');
INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES ('1', '5', '6', '3');