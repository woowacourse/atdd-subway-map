package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineEntity;

public interface LineDao {
    LineEntity save(Line line);

    boolean existByName(String name);

    List<LineEntity> findAll();

    LineEntity find(Long id);

    boolean existById(Long id);

    void update(Line line);

    void delete(Long id);
}
