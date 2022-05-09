package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface CommonLineDao {

    Line save(final Line line);

    List<Line> findAll();

    Line findById(final Long id);

    int update(final Long id, final Line line);

    int deleteById(final Long id);
}
