create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   created_at datetime,
   primary key(id)
);

create table if not exists LINE
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   color varchar(255) not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

create table if not exists LINE_STATION
(
    line bigint not null,
    station bigint not null,
    pre_station bigint,
    distance int,
    duration int,
    created_at datetime,
    updated_at datetime,
    index int
);

INSERT INTO line values (1, '1호선','bg-yellow-600', '05:30:00', '23:00:00', 10, '2020-05-09 14:38:44.901', '2020-05-09 14:38:44.901');
INSERT INTO line values (2, '2호선','bg-green-600', '05:20:00', '22:00:00', 4, '2020-05-09 14:38:55.394', '2020-05-09 14:38:55.394');

INSERT INTO station VALUES(1, '삼성역', '2020-05-09 14:38:44.901');
INSERT INTO station VALUES(2, '잠실역', '2020-05-09 14:38:46.901');
INSERT INTO station VALUES(3, '선릉역', '2020-05-09 14:50:44.901');
