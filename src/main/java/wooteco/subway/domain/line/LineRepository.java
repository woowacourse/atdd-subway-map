package wooteco.subway.domain.line;

import java.util.List;

public interface LineRepository {
    Line save(Line line);

    List<Line> allLines();

    Line findById(Long id);

    void update(Line line);

    void deleteById(Long id);

    boolean contains(Line line);
}
