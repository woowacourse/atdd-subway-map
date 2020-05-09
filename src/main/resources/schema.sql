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
   color varchar(255) not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

create table if not exists LINE_STATION
(
    line bigint references line(id),
    pre_station_id bigint,
    station_id bigint not null,
    distance integer not null,
    duration integer not null
);

-- insert into line values (1, '1호선', 'bg-blue-700', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into line values (2, '2호선', 'bg-blue-500', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into line values (3, '3호선', 'bg-orange-500', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into line values (4, '4호선', 'bg-orange-700', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
--
-- insert into station values (1, '삼성역', CURRENT_DATE());
-- insert into station values (2, '강남역', CURRENT_DATE());
-- insert into station values (3, '사당역', CURRENT_DATE());
