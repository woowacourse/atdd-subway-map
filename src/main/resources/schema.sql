drop table STATION if exists;
create table STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

drop table LINE if exists;
create table LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);