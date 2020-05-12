create table if not exists STATION
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    created_at datetime,
    primary key (id)
);

create table if not exists LINE
(
    id            bigint auto_increment not null,
    name          varchar(255)          not null,
    color         varchar(255)          not null,
    start_time    time                  not null,
    end_time      time                  not null,
    interval_time int                   not null,
    created_at    datetime,
    updated_at    datetime,
    primary key (id)
);

create table if not exists EDGE
(
    line           bigint references line (id),
    pre_station_id bigint,
    station_id     bigint not null,
    distance       integer,
    duration       integer
);
