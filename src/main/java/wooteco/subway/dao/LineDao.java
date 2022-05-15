package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.entity.LineEntity;

public interface LineDao {

    LineEntity save(LineEntity line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> update(Line line);

    Integer deleteById(Long id);
}
