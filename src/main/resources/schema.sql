CREATE TABLE IF NOT EXISTS station
(
    id   BIGINT AUTO_INCREMENT NOT NULL UNIQUE,
    name VARCHAR(255)          NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS line
(
    id    BIGINT AUTO_INCREMENT NOT NULL UNIQUE,
    name  VARCHAR(255)          NOT NULL UNIQUE,
    color VARCHAR(20)           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS section
(
    id              BIGINT AUTO_INCREMENT NOT NULL UNIQUE,
    line_id         BIGINT                NOT NULL,
    up_station_id   BIGINT                NOT NULL,
    down_station_id BIGINT                NOT NULL,
    distance        INT                   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (line_id) REFERENCES line (id) ON DELETE CASCADE,
    FOREIGN KEY (up_station_id) REFERENCES station (id),
    FOREIGN KEY (down_station_id) REFERENCES station (id)
);
