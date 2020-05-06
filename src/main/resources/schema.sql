CREATE TABLE IF NOT EXISTS STATION
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    created_at datetime,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS LINE
(
    id            bigint auto_increment not null,
    name          varchar(255)          not null,
    start_time    time                  not null,
    end_time      time                  not null,
    interval_time int                   not null,
    created_at    datetime,
    updated_at    datetime,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS LINE_STATION
(
    id             bigint auto_increment not null,
    line_id        bigint                not null,
    station_id     bigint                not null,
    pre_station_id bigint                not null,
    distance       int                   not null,
    duration       int                   not null,
    primary key (id)
)