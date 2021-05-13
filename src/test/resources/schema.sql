create table if not exists STATION
(
    id bigint not null auto_increment,
    name varchar(255) not null unique,
    primary key(id)
    );

create table if not exists LINE
(
    id bigint not null auto_increment,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
    );

create table if not exists SECTION
(
    id bigint not null auto_increment,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance bigint,
    primary key(id)
    );