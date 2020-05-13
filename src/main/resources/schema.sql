create table if not exists station
(
   id bigint auto_increment not null,
   name varchar(255) not null unique,
   created_at datetime,
   primary key(id)
);

create table if not exists line
(
   id bigint auto_increment not null,
   name varchar(255) not null unique,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   bg_color varchar(255) not null,
   primary key(id)
);

create table if not exists line_station
(
    line bigint not null,
    station bigint not null,
    pre_station bigint,
    distance int,
    duration int,
    created_at datetime,
    updated_at datetime,
    foreign key(line) references line (id),
    foreign key(station) references station (id)
);