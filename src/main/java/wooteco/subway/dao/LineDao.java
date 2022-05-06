package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Line updateById(Long id, Line line);

    Integer deleteById(Long id);
}
