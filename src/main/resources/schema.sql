drop table station if exists;
drop table line if exists;

create table STATION
(
    id   bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key (id)
);

create table LINE
(
    id    bigint auto_increment not null,
    name  varchar(255) not null unique,
    color varchar(20)  not null,
    primary key (id)
);