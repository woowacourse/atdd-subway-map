drop table if exists SECTION;
drop table if exists LINE;
drop table if exists STATION;


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

create table SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint not null,
    up_station_id   bigint not null,
    down_station_id bigint not null,
    distance        int,
    primary key (id)
);

alter table SECTION
    add constraint fk_section_to_line
        foreign key (line_id) references LINE (id)
            ON DELETE CASCADE ON UPDATE CASCADE;

alter table SECTION
    add constraint fk_section_to_up_station
        foreign key (up_station_id) references STATION (id)
            ON DELETE CASCADE ON UPDATE CASCADE;

alter table SECTION
    add constraint fk_section_to_down_station
        foreign key (down_station_id) references STATION (id)
            ON DELETE CASCADE ON UPDATE CASCADE;