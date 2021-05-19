create table if not exists STATION
(
    id
    bigint
    auto_increment
    not
    null,
    name
    varchar
(
    255
) not null unique,
    primary key
(
    id
)
    );

create table if not exists LINE
(
    id
    bigint
    auto_increment
    not
    null,
    name
    varchar
(
    255
) not null unique,
    color varchar
(
    20
) not null,
    first_station_id bigint,
    last_station_id bigint,
    primary key
(
    id
)
    );

create table if not exists SECTION
(
    id
    bigint
    auto_increment
    not
    null,
    line_id
    bigint
    not
    null,
    front_station_id
    bigint
    not
    null,
    back_station_id
    bigint
    not
    null,
    distance
    int,
    primary
    key
(
    id
)
    );