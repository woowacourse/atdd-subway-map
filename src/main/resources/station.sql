DROP TABLE Station IF EXISTS;
create table Station(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
)
