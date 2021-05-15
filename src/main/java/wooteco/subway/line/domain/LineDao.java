package wooteco.subway.line.domain;

import wooteco.subway.line.entity.LineEntity;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    LineEntity save(LineEntity lineEntity);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    Optional<LineEntity> findByName(String name);

    Optional<LineEntity> findByColor(String name);

    void clear();

    void update(Long id, String name, String color);

    void delete(Long id);

    boolean existByName(String name);

    boolean existByColor(String color);
}
