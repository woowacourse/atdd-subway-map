alter table SECTION alter column id restart with 1;
alter table LINE alter column id restart with 1;
alter table STATION alter column id restart with 1;

insert into STATION(name) values ('강남역');
insert into STATION(name) values ('역삼역');
insert into STATION(name) values ('선릉역');

insert into LINE (name, color) values ('2호선', 'bg-green-600');
