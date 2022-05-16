truncate table STATION restart identity;
alter table STATION alter column id restart with 1;

truncate table LINE restart identity;
alter table LINE alter column id restart with 1;

truncate table SECTION restart identity;
alter table SECTION alter column id restart with 1;
