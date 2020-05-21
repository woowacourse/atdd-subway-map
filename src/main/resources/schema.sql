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
    line_id bigint not null,
    station bigint not null,
    pre_station bigint,
    distance int,
    duration int,
    created_at datetime,
    updated_at datetime,
    index int
);
--
-- INSERT INTO STATION(ID, NAME)
-- VALUES (1, '강남');
--
-- INSERT INTO STATION(ID, NAME)
-- VALUES (2, '잠실');
--
-- INSERT INTO STATION(ID, NAME)
-- VALUES (3, '선릉');
--
-- INSERT INTO LINE(ID, NAME, COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (1, '1호선', 'bg-pink-600', '10:00:00', '10:00:00', 1);
--
-- INSERT INTO LINE(ID, NAME, COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (2, '2호선', 'bg-green-600', '10:00:00', '10:00:00', 1);
--
-- INSERT INTO LINE_STATION(LINE_ID, STATION, DISTANCE, DURATION, INDEX)
-- VALUES (1, 1, 1, 1, 1);