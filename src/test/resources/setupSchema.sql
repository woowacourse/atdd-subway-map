drop table if exists station;
drop table if exists line;

create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
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

insert into station (name) values ('선릉역');
insert into station (name) values ('잠실역');
insert into station (name) values ('강남역');

insert into line (name, color) values ('신분당선', 'green');
insert into line (name, color) values ('3호선', 'black');
insert into line (name, color) values ('1호선', 'red');
