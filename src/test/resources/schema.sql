drop table if exists SECTION;
drop table if exists LINE;
drop table if exists STATION;

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

create table if not exists SECTION
(
    id bigint auto_increment not null,
    lineId bigint not null,
    upStationId bigint not null,
    downStationId bigint not null,
    distance int,
    primary key(id),
    foreign key (lineId) references LINE(id),
    foreign key (upStationId) references STATION(id),
    foreign key (downStationId) references STATION(id)
);
