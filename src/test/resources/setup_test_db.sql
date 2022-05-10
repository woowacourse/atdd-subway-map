DROP TABLE station IF EXISTS;
DROP TABLE line IF EXISTS;
DROP TABLE section IF EXISTS;

CREATE TABLE station
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE line
(
    id    BIGINT AUTO_INCREMENT NOT NULL,
    name  VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE section
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    line_id         BIGINT NOT NULL,
    up_station_id   BIGINT NOT NULL,
    down_station_id BIGINT NOT NULL,
    distance        INT    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_section (line_id, up_station_id, down_station_id)
);
