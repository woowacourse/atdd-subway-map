CREATE TABLE IF NOT EXISTS Line
(
    id    integer     not null auto_increment,
    name  varchar(30) not null unique,
    color varchar(30) not null unique,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS Station
(
    id   integer     not null auto_increment,
    name varchar(30) not null unique,
    primary key (id)
);

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    primary key(id)
);