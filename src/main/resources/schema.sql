create table if not exists STATION
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    created_at datetime,
    primary key (id)
);

create table if not exists LINE
(
    id            bigint auto_increment not null,
    name          varchar(255)          not null,
    color         varchar(255)          not null,
    start_time    time                  not null,
    end_time      time                  not null,
    interval_time int                   not null,
    created_at    datetime,
    updated_at    datetime,
    primary key (id)
);

create table if not exists LINE_STATION
(
    line           bigint references line (id),
    pre_station_id bigint,
    station_id     bigint not null,
    distance       integer,
    duration       integer
);

-- insert into LINE values(1, '1호선', 'bg-blue-700', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(2, '2호선', 'bg-green-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(3, '3호선', 'bg-orange-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(4, '4호선', 'bg-blue-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(5, '5호선', 'bg-purple-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(6, '6호선', 'bg-yellow-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(7, '7호선', 'bg-green-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- insert into LINE values(8, '8호선', 'bg-pink-500', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
--
-- insert into STATION values(1, '수원', CURRENT_TIMESTAMP());
-- insert into STATION values(2, '화서', CURRENT_TIMESTAMP());
-- insert into STATION values(3, '성균관대', CURRENT_TIMESTAMP());
-- insert into STATION values(4, '교대', CURRENT_TIMESTAMP());
-- insert into STATION values(5, '강남', CURRENT_TIMESTAMP());
-- insert into STATION values(6, '역삼', CURRENT_TIMESTAMP());
-- insert into STATION values(7, '선릉', CURRENT_TIMESTAMP());
-- insert into STATION values(8, '삼성', CURRENT_TIMESTAMP());
-- insert into STATION values(9, '종합운동장', CURRENT_TIMESTAMP());
-- insert into STATION values(10, '잠실새내', CURRENT_TIMESTAMP());
-- insert into STATION values(11, '잠실', CURRENT_TIMESTAMP());
-- insert into STATION values(12, '당고개', CURRENT_TIMESTAMP());
-- insert into STATION values(13, '상계', CURRENT_TIMESTAMP());
-- insert into STATION values(14, '노원', CURRENT_TIMESTAMP());
-- insert into STATION values(15, '창동', CURRENT_TIMESTAMP());
-- insert into STATION values(16, '쌍문', CURRENT_TIMESTAMP());
--
-- insert into LINE_STATION values(1, null, 1, 0, 0);
-- insert into LINE_STATION values(1, 1, 2, 10, 2);
-- insert into LINE_STATION values(1, 2, 3, 10, 2);
-- insert into LINE_STATION values(2, null, 4, 0, 0);
-- insert into LINE_STATION values(2, 4, 5, 10, 2);
-- insert into LINE_STATION values(2, 5, 6, 10, 2);
-- insert into LINE_STATION values(2, 6, 7, 10, 2);
-- insert into LINE_STATION values(2, 7, 8, 10, 2);
-- insert into LINE_STATION values(2, 8, 9, 10, 2);
-- insert into LINE_STATION values(2, 9, 10, 10, 2);
-- insert into LINE_STATION values(2, 10, 11, 10, 2);