package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.LineEntity;

public interface LineDao {

    LineEntity save(LineEntity line);

    Optional<LineEntity> findById(Long id);

    Optional<LineEntity> findByName(String name);

    List<LineEntity> findAll();

    void update(LineEntity line);

    int deleteById(Long id);
}
