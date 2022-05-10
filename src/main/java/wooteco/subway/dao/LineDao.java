package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {
    Long save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    Long update(Long id, Line line);

    void deleteById(Long id);
}
