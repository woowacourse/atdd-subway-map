INSERT INTO station (name) VALUES ('a');
INSERT INTO station (name) VALUES ('b');
INSERT INTO station (name) VALUES ('c');

INSERT INTO line (name, start_time, end_time, interval_time, bg_color)
VALUES ('1호선', '06:00:00', '19:00:00', 12, 'bg-blue-200');

INSERT INTO line (name, start_time, end_time, interval_time, bg_color)
VALUES ('2호선', '06:00:00', '19:00:00', 12, 'bg-red-200');

INSERT INTO line (name, start_time, end_time, interval_time, bg_color)
VALUES ('3호선', '06:00:00', '19:00:00', 12, 'bg-green-200');