create table if not exists STATION
(
    id varchar(36) not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table if not exists LINE
(
    id varchar(36) not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table if not exists SECTION
(
    line_id varchar(36) not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
);