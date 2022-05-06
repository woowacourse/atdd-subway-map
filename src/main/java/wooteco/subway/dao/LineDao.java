package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    void deleteById(Long lineId);

    Line findById(Long lineId);

    void update(Long lineId, Line line);

    boolean existByName(Line line);

    boolean existByColor(Line line);

    boolean existByNameExceptSameId(Long lineId, Line line);

    boolean existByColorExceptSameId(Long lineId, Line line);
}
