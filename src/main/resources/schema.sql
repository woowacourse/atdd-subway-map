create table if not exists station
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key(id)
);

create table if not exists line
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null,
    primary key(id)
);

create table if not exists section
(
    id              bigint auto_increment not null,
    line_id         bigint                not null,
    up_station_id   bigint                not null,
    down_station_id bigint                not null,
    distance        int                   not null,
    primary key (id),
    foreign key (line_id)         references line (id) on delete cascade,
    foreign key (up_station_id)   references station (id),
    foreign key (down_station_id) references station (id)
);
