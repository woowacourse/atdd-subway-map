DROP TABLE IF EXISTS SECTION;
DROP TABLE IF EXISTS STATION;
DROP TABLE IF EXISTS LINE;


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
    primary key (id),
    foreign key (line_id) references line (id)
        on update cascade on delete cascade,
    foreign key (up_station_id) references station (id),
    foreign key (down_station_id) references station (id)
);
