create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
    );

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    stations varchar(1000) not null,
    distance int not null,
    color varchar(20) not null,
    extraFare bigint,
    primary key(id)
    );

-- create table if not exists SECTION
-- (
--     id bigint auto_increment not null,
--     line_id bigint not null,
--     up_station_id bigint not null,
--     down_station_id bigint not null,
--     distance int,
--     primary key(id)
--     );