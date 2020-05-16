create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) unique not null,
   created_at datetime,
   primary key(id)
);

create table if not exists LINE
(
   id bigint auto_increment not null,
   name varchar(255) unique not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   background_color varchar(255),
   primary key(id)
);

create table if not exists line_station (
    line bigint,
    line_key int,
    station_id bigint,
    pre_station_id bigint,
    distance int,
    duration int
);