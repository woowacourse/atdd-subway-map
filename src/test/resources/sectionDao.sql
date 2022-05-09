drop table section if exists;

create table SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint not null,
    up_station_id   bigint not null,
    down_station_id bigint not null,
    distance        bigint not null,
    primary key (id)
--     foreign key (line_id) references LINE(id),
--     foreign key (up_station_id) references  STATION(id),
--     foreign key (down_station_id) references STATION(id)
);