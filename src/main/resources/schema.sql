create table if not exists STATION
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key (id)
);

create table if not exists LINE
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null unique,
    primary key (id)
);

create table if not exists SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint                not null,
    up_station_id   bigint                not null,
    down_station_id bigint                not null,
    distance        int,
    primary key (id),
    constraint section_line_id_fk foreign key (line_id) references LINE (id)
        on update cascade on delete cascade
);

insert into station(name)
values ('강남역');
insert into station(name)
values ('선릉역');
insert into station(name)
values ('잠실역');
insert into station(name)
values ('대림역');
insert into station(name)
values ('서초역');

insert into line(name, color)
values ('2호선', 'bg-200-green');
insert into line(name, color)
values ('3호선', 'bg-300-orange');
insert into line(name, color)
values ('7호선', 'bg-700-kaki');
insert into line(name, color)
values ('4호선', 'bg-400-blue');
