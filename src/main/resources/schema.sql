CREATE TABLE IF NOT EXISTS station
(
    id   BIGINT auto_increment NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS line
(
    id    BIGINT auto_increment NOT NULL,
    name  VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(20) NOT NULL UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS section
(
    id              BIGINT auto_increment NOT NULL,
    line_id         BIGINT NOT NULL,
    up_station_id   BIGINT NOT NULL,
    down_station_id BIGINT NOT NULL,
    distance        INT,
    PRIMARY KEY(id)
);
