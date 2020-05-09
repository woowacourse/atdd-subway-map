create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   created_at datetime,
   primary key(id)
);

-- 1호선
insert into STATION (name) values ('수원');
insert into STATION (name) values ('화성');
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

insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('1호선', '06:00:00', '18:00:00', '1', 'bg-blue-700');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('2호선', '07:00:00', '19:00:00', '2', 'bg-green-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('3호선', '08:00:00', '20:00:00', '3', 'bg-orange-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('4호선', '09:00:00', '21:00:00', '4', 'bg-blue-500');
insert into LINE (name, start_time, end_time, interval_time, bg_color) values ('5호선', '10:00:00', '22:00:00', '5', 'bg-purple-500');

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