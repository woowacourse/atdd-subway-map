drop table SECTION if exists;
drop table LINE if exists;
drop table STATION if exists;

create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key (id)
    );

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key (id)
    );

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    primary key (id),
    foreign key (line_id) references LINE (id) on delete cascade
    );


insert into STATION(name) values ('강남역');
insert into STATION(name) values ('역삼역');
insert into STATION(name) values ('선릉역');

insert into LINE (name, color) values ('2호선', 'bg-green-600');
