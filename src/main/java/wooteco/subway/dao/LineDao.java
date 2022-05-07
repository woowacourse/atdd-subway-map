package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    boolean existsName(Line line);

    Line save(final Line line);

    Optional<Line> findById(Long id);

    List<Line> findAll();

    void update(Line line);

    void delete(Line line);
}
