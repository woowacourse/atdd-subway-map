DROP TABLE Line IF EXISTS;

create table Line(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);