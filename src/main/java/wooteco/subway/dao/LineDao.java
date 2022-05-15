package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.entity.LineEntity;

public interface LineDao {

    LineEntity save(LineEntity line);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    Optional<LineEntity> update(Line line);

    Integer deleteById(Long id);
}
