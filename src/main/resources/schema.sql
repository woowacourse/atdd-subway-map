CREATE TABLE IF NOT EXISTS Line (
    id integer not null auto_increment,
    name varchar(30) not null,
    color varchar(30) not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS Station (
    id integer not null auto_increment,
    name varchar(30) not null,
    primary key (id)
)