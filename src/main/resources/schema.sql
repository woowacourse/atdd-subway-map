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
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

create table if not exists line_station (
    line bigint,
    station_id bigint,
    pre_station_id bigint,
    distance int,
    duration int
);

insert into line values (1, '1호선', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into line values (2, '2호선', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into line values (3, '3호선', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into line values (4, '4호선', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());