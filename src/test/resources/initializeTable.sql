-- CREATE DATABASE subway DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
-- -- USE subway;

DROP TABLE SECTION;

DROP TABLE LINE;

DROP TABLE STATION;

CREATE TABLE if not exists STATION (
    id bigint auto_increment not null,
    name varchar ( 255 ) not null unique,
    primary key ( id )
);

CREATE TABLE if not exists LINE (
    id bigint auto_increment not null,
    name varchar ( 255 ) not null unique,
    color varchar ( 20 ) not null unique,
    primary key ( id )
);

CREATE TABLE if not exists SECTION (
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int, primary key ( id )
);