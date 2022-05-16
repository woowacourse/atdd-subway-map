alter table LINE alter column id restart with 1;
alter table STATION alter column id restart with 1;

insert into STATION (name) values ('강남역');
insert into STATION (name) values ('역삼역');

insert into LINE (name, color) values ('2호선', 'bg-green-600');
insert into LINE (name, color) values ('신분당선', 'bg-red-600');
