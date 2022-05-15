drop table if exists SECTION;
drop table if exists STATION;
drop table if exists LINE;

create table STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table SECTION
(
    id bigint auto_increment not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    line_id bigint not null,
    primary key(id),
    constraint fk_line_id foreign key(line_id) references LINE(id) on delete cascade,
    constraint fk_up_station_id foreign key(up_station_id) references STATION(id) on delete cascade,
    constraint fk_down_station_id foreign key(down_station_id) references STATION(id) on delete cascade
);
