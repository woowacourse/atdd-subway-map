drop table "SECTION" if exists;
drop table LINE if exists;
drop table STATION if exists;

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
    color varchar(20) not null unique,
    primary key(id)
    );

create table if not exists "SECTION"
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    line_order bigint not null,
    primary key (id),
    foreign key (line_id) references LINE(id) on delete cascade,
    foreign key (up_station_id) references STATION(id),
    foreign key (down_station_id) references STATION(id)
    );

INSERT INTO STATION (name)
VALUES ('신도림역');
INSERT INTO STATION (name)
VALUES ('왕십리역');
INSERT INTO STATION (name)
VALUES ('용산역');
INSERT INTO STATION (name)
VALUES ('역곡역');