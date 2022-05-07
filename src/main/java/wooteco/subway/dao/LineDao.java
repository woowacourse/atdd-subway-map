package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    boolean existsByName(String name);

    List<Line> findAll();

    Line findById(Long id);

    void update(Long id, Line updateLine);

    void deleteById(Long id);
}
