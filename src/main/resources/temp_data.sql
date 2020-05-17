insert into LINE (name, start_time, end_time, interval_time, bg_color, created_at, updated_at)
values ('0호선', '07:00', '23:00', 5, 'bg-pink-900', '20100301', '20100301'),
       ('9호선', '07:00', '23:00', 5, 'bg-pink-900', '20100301', '20100301');

insert into STATION (name, created_at)
values ('압구정로데오역', '20100301'),
       ('낙성대역', '20100301'),
       ('오리역', '20100301');

insert into LINE_STATION (line, line_key, station_id, pre_station_id, distance, duration)
values (1, 0, 1, null, 1, 1),
       (1, 1, 2, 1, 1, 1)
