package wooteco.subway.dao.line;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line create(Line line);

    Line show(Long id);

    List<Line> showAll();

    int update(long id, Line line);

    int delete(long id);
}
