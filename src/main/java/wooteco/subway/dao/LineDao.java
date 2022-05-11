package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Optional<Line> findById(Long id);

    List<Line> findAll();

    Long updateByLine(Line line);

    int deleteById(Long id);
}
