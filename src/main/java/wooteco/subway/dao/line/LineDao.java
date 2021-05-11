package wooteco.subway.dao.line;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Optional<Line> findLineByName(String name);

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findCompleteLineById(Long id);

    void removeLine(Long id);

    void update(Line line);

    Optional<Line> findLineByNameOrColor(String name, String color, Long lineId);
}
