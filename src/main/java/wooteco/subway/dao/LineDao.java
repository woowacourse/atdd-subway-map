package wooteco.subway.dao;
import wooteco.subway.domain.Line;

import java.util.List;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Line find(Long id);

    int update(Long id, Line line);

    int delete(Long id);
}
