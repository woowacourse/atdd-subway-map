drop table STATION if exists;
drop table SECTION if exists;
drop table LINE if exists;


create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    up_station_id bigint not null,
    down_station_id bigint not null,
    color varchar(20) not null,
    distance int,
    primary key(id)
    );

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    primary key(id),
    foreign key (line_id) references line (id)
    );


create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    section_id bigint,
    primary key(id),
    foreign key (section_id) references section (id)
    );
