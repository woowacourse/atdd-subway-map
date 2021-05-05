package wooteco.subway.line;

import java.util.List;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Long id, Line newLine);

    void remove(Long id);
}
