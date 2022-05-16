insert into station (name) values ('강남역');
insert into station (name) values ('왕십리역');
insert into line (name, color) values ('신분당선','red');
insert into line (name, color) values ('2호선','green');
insert into section (line_id, up_station_id, down_station_id, distance) values (1, 2, 1, 5);