package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Optional<Line> insert(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> updateById(Long id, Line line);

    Integer deleteById(Long id);
}
