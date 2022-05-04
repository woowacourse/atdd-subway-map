package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    int deleteById(Long id);

    Optional<Line> findById(Long id);

    boolean exists(Line line);

    int update(Long id, Line updatingLine);
}
