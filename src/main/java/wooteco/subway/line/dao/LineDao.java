package wooteco.subway.line.dao;

import wooteco.subway.domain.Line;

import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    Optional<Line> findByNameAndColor(String name, String color);

    Optional<Line> findById(Long lineId);
}
