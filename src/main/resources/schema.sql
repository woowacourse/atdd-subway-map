DROP TABLE if exists LINE;
DROP TABLE if exists STATION;
DROP TABLE if exists LINE_STATION;

create table if not exists STATION
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null unique,
    created_at datetime,
    primary key (id)
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
    line           bigint  not null,
    pre_station_id bigint,
    station_id     bigint  not null,
    distance       integer not null,
    duration       integer not null,
    created_at     datetime,
    updated_at     datetime
);

ALTER TABLE LINE
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE STATION
    ALTER COLUMN id RESTART WITH 1;





