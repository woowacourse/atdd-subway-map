package wooteco.subway.dao.line;

import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    Optional<Line> findByNameAndColor(String name, String color);

    Optional<Line> findById(Long lineId);

    List<Line> findAll();

    void delete(Long lineId);
}
