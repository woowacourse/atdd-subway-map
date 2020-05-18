CREATE TABLE IF NOT EXISTS station
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    created_at datetime,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS line
(
    id            bigint auto_increment not null,
    name          varchar(255)          not null,
    start_time    time                  not null,
    end_time      time                  not null,
    interval_time int                   not null,
    created_at    datetime,
    updated_at    datetime,
    color_type    varchar(255)          not null,
    color_value   varchar(255)          not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS edge
(
    line           bigint                not null,
    station_id     bigint                not null,
    pre_station_id bigint                ,
    distance       int                   not null,
    duration       int                   not null,
    sequence       int                   not null,
    created_at     datetime,
    updated_at     datetime
);
