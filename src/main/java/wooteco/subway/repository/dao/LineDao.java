package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.repository.dao.entity.line.LineEntity;

public interface LineDao {

    Long save(LineEntity lineEntity);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    Boolean existsById(Long id);

    Boolean existsByName(String name);

    Boolean existsByColor(String color);

    void update(LineEntity lineEntity);

    void remove(Long id);
}
