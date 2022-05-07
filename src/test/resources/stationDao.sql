drop table station if exists;

create table STATION
(
    id   bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key (id)
);