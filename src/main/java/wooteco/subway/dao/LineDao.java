package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.entity.LineEntity;

public interface LineDao {

    LineEntity save(LineEntity line);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    int update(LineEntity line);

    Integer deleteById(Long id);
}
