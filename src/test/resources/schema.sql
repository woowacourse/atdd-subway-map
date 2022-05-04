DROP TABLE IF EXISTS STATION;
DROP TABLE IF EXISTS LINE;

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
