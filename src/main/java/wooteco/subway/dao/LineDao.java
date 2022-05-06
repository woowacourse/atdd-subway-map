package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    Line update(Long id, Line updateLine);

    void deleteById(Long id);
}
