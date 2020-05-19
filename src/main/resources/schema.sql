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
   title varchar(255) not null,
   start_time time not null,
   end_time time not null,
   interval_time int not null,
   created_at datetime,
   updated_at datetime,
   bg_color varchar(255) not null,
   primary key(id)
);

create table if not exists EDGE
(
    line bigint not null,
    sequence int not null,
    station_id bigint not null,
    pre_station_id bigint,
    distance int,
    duration int,
    created_at datetime,
    updated_at datetime
);

-- INSERT INTO STATION(ID, NAME)
-- VALUES (1, '잠실');
-- INSERT INTO STATION(ID, NAME)
-- VALUES (2, '교대');
-- INSERT INTO STATION(ID, NAME)
-- VALUES (3, '강남');
-- INSERT INTO LINE(ID, TITLE, BG_COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (1, '2호선', 'bg-pink-600', '10:00:00', '10:00:00', 1);
-- INSERT INTO LINE(ID, TITLE, BG_COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (2, '3호선', 'bg-green-600', '10:00:00', '10:00:00', 1);
-- INSERT INTO LINE_STATION(LINE, SEQUENCE, STATION_ID, DISTANCE, DURATION)
-- VALUES (1, 1, 1, 0, 0);