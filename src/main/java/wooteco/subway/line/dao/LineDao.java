package wooteco.subway.line.dao;

import java.util.List;
import wooteco.subway.line.Line;

public interface LineDao {

    Line save(Line line);

    Line show(Long id);

    List<Line> showAll();

    int update(long id, Line line);

    int delete(long id);
}
