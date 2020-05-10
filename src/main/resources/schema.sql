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
   bg_color varchar(255) not null,
   created_at datetime,
   updated_at datetime,
   primary key(id)
);

create table if not exists LINE_STATION
(
    line bigint not null,
    station_id bigint not null,
    pre_station_id bigint,
    distance int,
    duration int
);

INSERT INTO LINE(name, start_time, end_time, interval_time, bg_color) VALUES ('1호선', PARSEDATETIME('06:00', 'HH:mm'), PARSEDATETIME('23:00', 'HH:mm'), 10, 'bg-blue-700');
INSERT INTO LINE(name, start_time, end_time, interval_time, bg_color) VALUES ('2호선', PARSEDATETIME('07:00', 'HH:mm'), PARSEDATETIME('00:00', 'HH:mm'), 10, 'bg-green-500');
INSERT INTO LINE(name, start_time, end_time, interval_time, bg_color) VALUES ('4호선', PARSEDATETIME('08:00', 'HH:mm'), PARSEDATETIME('22:00', 'HH:mm'), 10, 'bg-blue-500');

INSERT INTO STATION(name) VALUES ('당산');
INSERT INTO STATION(name) VALUES ('합정');
INSERT INTO STATION(name) VALUES ('홍대입구');
INSERT INTO STATION(name) VALUES ('신촌');
INSERT INTO STATION(name) VALUES ('이대');
INSERT INTO STATION(name) VALUES ('아현');
INSERT INTO STATION(name) VALUES ('충정로');
INSERT INTO STATION(name) VALUES ('시청');
INSERT INTO STATION(name) VALUES ('서울역');
INSERT INTO STATION(name) VALUES ('남영');
INSERT INTO STATION(name) VALUES ('용산');
INSERT INTO STATION(name) VALUES ('노량진');
INSERT INTO STATION(name) VALUES ('숙대입구');
INSERT INTO STATION(name) VALUES ('삼각지');
INSERT INTO STATION(name) VALUES ('신용산');
INSERT INTO STATION(name) VALUES ('이촌');
INSERT INTO STATION(name) VALUES ('동작');
INSERT INTO STATION(name) VALUES ('이수');
