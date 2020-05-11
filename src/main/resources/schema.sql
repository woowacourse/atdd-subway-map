CREATE TABLE IF NOT EXISTS STATION
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    created_at datetime,
    primary key (id),
    unique (name)
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
    color         varchar(255)          not null,
    primary key (id),
    unique (name)
);

CREATE TABLE IF NOT EXISTS EDGE
(
    id             bigint auto_increment not null,
    line_id        bigint                not null,
    line_key       bigint                not null,
    station_id     bigint                not null,
    pre_station_id bigint,
    distance       int,
    duration       int,
    primary key (id)
);

ALTER TABLE EDGE
    ADD FOREIGN KEY (line_id)
        REFERENCES LINE (id);

ALTER TABLE EDGE
    ADD FOREIGN KEY (station_id)
        REFERENCES STATION (id);