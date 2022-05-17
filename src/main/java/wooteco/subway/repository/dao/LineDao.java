package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.LineEntity;

public interface LineDao {

    LineEntity save(LineEntity line);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    int update(LineEntity line);

    Integer deleteById(Long id);
}
