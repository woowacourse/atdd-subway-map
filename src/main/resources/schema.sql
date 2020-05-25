create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    created_at timestamp default current_timestamp,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    start_time time not null,
    end_time time not null,
    interval_time int not null,
    bg_color varchar(255) not null unique,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp ,
   primary key(id)
);
create table if not exists LINE_STATION
(
    line bigint not null,
    line_key bigint not null,
    station bigint not null,
    pre_station bigint,
    distance int,
    duration int,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);