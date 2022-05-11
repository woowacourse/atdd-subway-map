drop table if exists section;
drop table if exists station;
drop table if exists line;

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
    primary key (id),
    constraint section_line_id_fk foreign key (line_id) references LINE (id)
        on update cascade on delete cascade
);
