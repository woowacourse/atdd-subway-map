create table if not exists STATION
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key (id)
);

create table if not exists LINE
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null unique,
    primary key (id)
);

create table if not exists SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint                not null,
    up_station_id   bigint                not null,
    down_station_id bigint                not null,
    distance        int,
    primary key (id)
);

TRUNCATE TABLE STATION RESTART IDENTITY;
ALTER TABLE STATION
    ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE LINE RESTART IDENTITY;
ALTER TABLE LINE
    ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE SECTION RESTART IDENTITY;
ALTER TABLE LINE
    ALTER COLUMN id RESTART WITH 1;

