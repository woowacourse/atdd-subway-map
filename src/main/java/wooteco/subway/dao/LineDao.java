package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.entity.LineEntity;

public interface LineDao {
    LineEntity save(LineEntity entity);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    boolean update(LineEntity entity);

    boolean delete(Long id);
}
