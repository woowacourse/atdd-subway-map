drop table section if exists;

create table SECTION
(
    id bigint auto_increment not null,
    lineId bigint not null,
    upStationId bigint not null,
    downStationId bigint not null,
    distance int,
    primary key(id)
);