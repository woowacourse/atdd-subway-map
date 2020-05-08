create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   created_at datetime,
   primary key(id)
);

-- 1호선
insert into STATION (name) values ('수원');
insert into STATION (name) values ('화서');
insert into STATION (name) values ('성균관대');

-- 2호선
insert into STATION (name) values ('교대');
insert into STATION (name) values ('강남');
insert into STATION (name) values ('역삼');
insert into STATION (name) values ('선릉');
insert into STATION (name) values ('삼성');
insert into STATION (name) values ('종합운동장');
insert into STATION (name) values ('잠실새내');
insert into STATION (name) values ('잠실');

create table if not exists LINE
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   bg_color varchar(255) not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('1호선', '06:00:00', '22:00:00', '10', 'bg-blue-700');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('2호선', '06:00:00', '22:00:00', '10', 'bg-green-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('3호선', '06:00:00', '22:00:00', '10', 'bg-orange-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('4호선', '06:00:00', '22:00:00', '10', 'bg-blue-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('5호선', '06:00:00', '22:00:00', '10', 'bg-purple-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('6호선', '06:00:00', '22:00:00', '10', 'bg-yellow-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('7호선', '06:00:00', '22:00:00', '10', 'bg-green-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('8호선', '06:00:00', '22:00:00', '10', 'bg-pink-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('신분당선', '06:00:00', '22:00:00', '10', 'bg-red-500');

create table if not exists LINE_STATION
(
    line bigint not null,
    station bigint not null,
    pre_station bigint,
    distance int,
    duration int,
    created_at datetime,
    updated_at datetime
);

insert into LINE_STATION (line, station, pre_station, distance, duration) values (1, 1, null, 10, 10);
insert into LINE_STATION (line, station, pre_station, distance, duration) values (1, 2, 1, 10, 10);
insert into LINE_STATION (line, station, pre_station, distance, duration) values (1, 3, 2, 10, 10);