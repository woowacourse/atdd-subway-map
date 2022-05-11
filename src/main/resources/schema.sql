CREATE TABLE if not exists STATION
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

CREATE TABLE if not exists LINE
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
    primary key
(
    id
)
    );

CREATE TABLE IF NOT EXISTS "SECTION"
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
    up_station_id
    bigint
    not
    null,
    down_station_id
    bigint
    not
    null,
    distance
    int
    not
    null,
    line_order
    bigint
    not
    null,
    primary
    key
(
    id
),
    foreign key
(
    line_id
) references LINE
(
    id
) on delete cascade,
    foreign key
(
    up_station_id
) references STATION
(
    id
),
    foreign key
(
    down_station_id
) references STATION
(
    id
)
    );

