create table if not exists STATION
(
   id bigint auto_increment not null,
   name varchar(255) not null,
   created_at datetime,
   primary key(id)
);

create table if not exists LINE
(
    id            bigint auto_increment not null,
    name          varchar(255)          not null,
    color         varchar(20)           not null,
    start_time    time                  not null,
    end_time      time                  not null,
    interval_time int                   not null,
    created_at    datetime,
    updated_at    datetime,
    primary key (id)
);

create table if not exists LINE_STATION
(
    line        bigint not null,
    sequence    int    not null,
    station     bigint not null,
    pre_station bigint,
    distance    int,
    duration    int,
    created_at  datetime,
    updated_at  datetime
);

-- INSERT INTO STATION(ID, NAME)
-- VALUES (1, 'TEST_STATION1');
--
-- INSERT INTO STATION(ID, NAME)
-- VALUES (2, 'TEST_STATION2');
--
-- INSERT INTO STATION(ID, NAME)
-- VALUES (3, 'TEST_STATION3');
--
-- INSERT INTO LINE(ID, NAME, COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (1, 'TEST_LINE1', 'bg-pink-600', '10:00:00', '10:00:00', 1);
--
-- INSERT INTO LINE(ID, NAME, COLOR, START_TIME, END_TIME, INTERVAL_TIME)
-- VALUES (2, 'TEST_LINE2', 'bg-green-600', '10:00:00', '10:00:00', 1);
--
-- INSERT INTO LINE_STATION(LINE, SEQUENCE, STATION, DISTANCE, DURATION)
-- VALUES (1, 1, 1, 0, 0);