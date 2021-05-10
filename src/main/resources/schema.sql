create table if not exists STATION (
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    PRIMARY KEY(id)
    );

create table if not exists LINE (
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    PRIMARY KEY(id)
    );

create table if not exists SECTION (
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    PRIMARY KEY(id),
    FOREIGN KEY (line_id) REFERENCES line(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (up_station_id) REFERENCES station(id) ON UPDATE CASCADE,
    FOREIGN KEY (down_station_id) REFERENCES station(id) ON UPDATE CASCADE
    );