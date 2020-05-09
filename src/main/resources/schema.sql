create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   created_at datetime,
   primary key(id)
);

create table if not exists LINE
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   bg_color varchar(255) not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

create table if not exists LINE_STATION
(
    line bigint not null,
    pre_station_id bigint,
    station_id bigint not null,
    distance int not null,
    duration int not null
);
