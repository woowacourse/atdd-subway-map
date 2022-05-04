package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    boolean deleteById(Long id);

    Line findById(Long id);

    boolean updateById(Long id, Line line);
}
