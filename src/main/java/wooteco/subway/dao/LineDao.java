package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Line findById(Long id);

    List<Line> findAll();

    Long updateByLine(Line line);

    int deleteById(Long id);
}
