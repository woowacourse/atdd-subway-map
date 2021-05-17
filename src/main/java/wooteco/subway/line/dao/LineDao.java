package wooteco.subway.line.dao;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineColor;
import wooteco.subway.line.domain.LineName;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    boolean checkExistName(LineName name);

    boolean checkExistColor(LineColor color);

    boolean checkExistId(Long id);

    void update(Line line);

    void delete(Long lineId);
}
