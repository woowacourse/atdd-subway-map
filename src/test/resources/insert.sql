insert into STATION (name)
values ('암사역'),
       ('천호역'),
       ('몽촌토성역'),
       ('잠실역');

insert into LINE (name, start_time, end_time, interval_time, bg_color)
VALUES ('8호선', '05:40', '23:57', '8', 'bg-pink-500');

insert into LINE_STATION (line, station_id, pre_station_id, distance, duration)
values (1, 1, null, 0, 0),
       (1, 2, 1, 3, 3),
       (1, 3, 2, 3, 3),
       (1, 4, 3, 3, 3);

