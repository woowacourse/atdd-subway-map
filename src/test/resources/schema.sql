DROP TABLE station IF EXISTS;
DROP TABLE line IF EXISTS;
DROP TABLE section IF EXISTS;

CREATE TABLE station
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY(id)
);

-- CREATE TABLE line
-- (
--     id BIGINT AUTO_INCREMENT NOT NULL,
--     name VARCHAR(255) NOT NULL UNIQUE,
--     color VARCHAR(20) NOT NULL,
--     PRIMARY KEY(id)
-- );
--
-- CREATE TABLE section
-- (
--     id BIGINT AUTO_INCREMENT NOT NULL,
--     line_id BIGINT NOT NULL,
--     up_station_id BIGINT NOT NULL,
--     down_station_id BIGINT NOT NULL,
--     distance INT,
--     PRIMARY KEY(id)
-- );

INSERT INTO station(id, name)
VALUES (1, '중복되는 역 이름'),
       (2, '선릉역'),
       (3, '잠실역');
