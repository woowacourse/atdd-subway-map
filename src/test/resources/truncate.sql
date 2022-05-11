truncate table SECTION;
truncate table STATION;
truncate table LINE;

alter table SECTION alter id restart with 1;
alter table STATION alter id restart with 1;
alter table LINE alter id restart with 1;
