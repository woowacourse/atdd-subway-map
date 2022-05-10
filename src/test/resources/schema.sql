DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS station;
DROP TABLE IF EXISTS line;

CREATE TABLE station
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE line
(
    id    BIGINT AUTO_INCREMENT NOT NULL,
    name  VARCHAR(255)          NOT NULL UNIQUE,
    color VARCHAR(20)           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE section
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    line_id         BIGINT                NOT NULL,
    up_station_id   BIGINT                NOT NULL,
    down_station_id BIGINT                NOT NULL,
    distance        INT                   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (line_id) REFERENCES line (id) ON DELETE CASCADE,
    FOREIGN KEY (up_station_id) REFERENCES station (id) ON DELETE CASCADE,
    FOREIGN KEY (down_station_id) REFERENCES station (id) ON DELETE CASCADE
);