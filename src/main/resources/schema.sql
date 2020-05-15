create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null unique,
   created_at datetime,
   primary key(id)
);

create table if not exists LINE
(
   id bigint auto_increment not null,
   name varchar(255) not null unique,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   color varchar(20),
   primary key(id)
);

create table if not exists LINESTATION
(
    id bigint auto_increment not null,
    line bigint not null,
    subway_operating_sequence int not null,
    station_id bigint not null,
    pre_station_id bigint,
    distance int not null,
    duration int not null,
    primary key(id),
    foreign key(line) references LINE(id),
    foreign key(station_id) references STATION(id),
    foreign key(pre_station_id) references STATION(id)
);