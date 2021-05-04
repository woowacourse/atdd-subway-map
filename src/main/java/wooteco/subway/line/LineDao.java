package wooteco.subway.line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Optional<Line> findLineByName(String name);

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findLineById(Long id);

    void removeLine(Long id);
}
