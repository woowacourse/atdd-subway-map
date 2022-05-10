package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> update(Line line);

    Integer deleteById(Long id);
}
