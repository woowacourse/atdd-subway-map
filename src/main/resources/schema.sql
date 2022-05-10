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
    upStationId bigint not null,
    downStationId bigint not null,
    lineId bigint not null,
    foreign key(upStationId) references STATION(id) on delete cascade,
    foreign key(downStationId) references STATION(id) on delete cascade,
    foreign key(lineId) references LINE(id) on delete cascade,
    distance int not null
);
