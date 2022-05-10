
drop table if exists station;
drop table if exists section;
drop table if exists line;

create table station
(
    id   bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key (id)
);

create table line
(
    id    bigint auto_increment not null,
    name  varchar(255) not null unique,
    color varchar(20)  not null,
    primary key (id)
);

create table section
(
    id              bigint auto_increment not null,
    line_id         bigint not null,
    up_station_id   bigint not null,
    down_station_id bigint not null,
    distance        int    not null,
    primary key (id),
    foreign key (line_id) references line (id) on delete cascade
);

INSERT INTO station (name) VALUES ('합정역');
INSERT INTO station (name) VALUES ('홍대입구역');
INSERT INTO station (name) VALUES ('신촌역');

INSERT INTO line (name, color) VALUES ('2호선', 'bg-green-600');

INSERT INTO section (line_id, up_station_id, down_station_id, distance)
VALUES ('1', '1', '2', '5');

INSERT INTO section (line_id, up_station_id, down_station_id, distance)
VALUES ('1', '2', '3', '5');

