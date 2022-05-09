DROP TABLE station IF EXISTS;
DROP TABLE line IF EXISTS;

CREATE TABLE station
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE line
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(20) NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO station(id, name)
VALUES (1, '이미 존재하는 역 이름'),
       (2, '선릉역'),
       (3, '잠실역');

INSERT INTO line(id, name, color)
VALUES (1, '이미 존재하는 노선 이름', '노란색'),
       (2, '신분당선', '빨간색'),
       (3, '2호선', '초록색');
