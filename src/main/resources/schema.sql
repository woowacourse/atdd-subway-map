DROP TABLE IF EXISTS STATION;
DROP TABLE IF EXISTS LINE;
DROP TABLE IF EXISTS SECTION;

CREATE TABLE STATION
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key (id)
);

CREATE TABLE LINE
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null,
    primary key (id)
);

create table SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint                not null,
    up_station_id   bigint                not null,
    down_station_id bigint                not null,
    distance        int,
    primary key (id)
);
